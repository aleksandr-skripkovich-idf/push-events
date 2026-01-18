package com.test.saga.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.saga.model.*;
import com.test.saga.repository.SagaStepRepository;
import com.test.saga.repository.SagaTransactionRepository;
import com.test.saga.step.StepExecutionException;
import com.test.saga.step.StepHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SagaOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(SagaOrchestrator.class);
    private static final Random RANDOM = new Random();

    private final SagaTransactionRepository transactionRepository;
    private final SagaStepRepository stepRepository;
    private final Map<String, StepHandler> stepHandlers;
    private final ObjectMapper objectMapper;

    public SagaOrchestrator(SagaTransactionRepository transactionRepository,
                          SagaStepRepository stepRepository,
                          List<StepHandler> handlers,
                          ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.stepRepository = stepRepository;
        this.objectMapper = objectMapper;
        this.stepHandlers = new HashMap<>();
        handlers.forEach(h -> stepHandlers.put(h.getStepName(), h));
    }

    @Async
    public void startSaga(UUID sagaId) {
        log.info("Starting SAGA transaction: {}", sagaId);

        SagaTransaction saga = createSagaTransaction(sagaId);
        transactionRepository.save(saga);

        executeSaga(saga);
    }

    private SagaTransaction createSagaTransaction(UUID sagaId) {
        SagaTransaction saga = new SagaTransaction(sagaId);

        UUID customerId = UUID.randomUUID();
        String productName = getRandomProduct();
        int quantity = RANDOM.nextInt(10) + 1;
        BigDecimal amount = BigDecimal.valueOf(RANDOM.nextDouble() * 1000 + 50).setScale(2, BigDecimal.ROUND_HALF_UP);

        try {
            String orderInput = objectMapper.writeValueAsString(Map.of(
                    "customerId", customerId.toString(),
                    "productName", productName,
                    "quantity", quantity,
                    "amount", amount.toString()
            ));
            SagaStep orderStep = new SagaStep(UUID.randomUUID(), 1, "CREATE_ORDER", orderInput);
            saga.addStep(orderStep);

            String inventoryInput = objectMapper.writeValueAsString(Map.of(
                    "productName", productName,
                    "quantity", quantity
            ));
            SagaStep inventoryStep = new SagaStep(UUID.randomUUID(), 2, "RESERVE_INVENTORY", inventoryInput);
            saga.addStep(inventoryStep);

            String paymentInput = objectMapper.writeValueAsString(Map.of(
                    "customerId", customerId.toString(),
                    "amount", amount.toString()
            ));
            SagaStep paymentStep = new SagaStep(UUID.randomUUID(), 3, "PROCESS_PAYMENT", paymentInput);
            saga.addStep(paymentStep);

        } catch (Exception e) {
            log.error("Error creating saga steps", e);
        }

        return saga;
    }

    @Transactional
    public void executeSaga(SagaTransaction saga) {
        saga.setStatus(SagaStatus.IN_PROGRESS);
        transactionRepository.save(saga);

        List<SagaStep> steps = saga.getSteps();

        for (int i = 0; i < steps.size(); i++) {
            SagaStep step = steps.get(i);
            saga.setCurrentStep(i + 1);

            StepHandler handler = stepHandlers.get(step.getStepName());
            if (handler == null) {
                log.error("No handler found for step: {}", step.getStepName());
                failSaga(saga, "No handler for step: " + step.getStepName());
                return;
            }

            step.setStatus(StepStatus.EXECUTING);
            stepRepository.save(step);

            try {
                handler.execute(step);
                step.setStatus(StepStatus.COMPLETED);
                step.setExecutedAt(LocalDateTime.now());
                stepRepository.save(step);
                log.info("Step {} completed for saga {}", step.getStepName(), saga.getId());

            } catch (StepExecutionException e) {
                log.error("Step {} failed for saga {}: {}", step.getStepName(), saga.getId(), e.getMessage());
                step.setStatus(StepStatus.FAILED);
                step.setErrorMessage(e.getMessage());
                stepRepository.save(step);

                failSaga(saga, e.getMessage());
                compensate(saga, i);
                return;
            }
        }

        saga.setStatus(SagaStatus.COMPLETED);
        saga.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(saga);
        log.info("SAGA {} completed successfully", saga.getId());
    }

    private void failSaga(SagaTransaction saga, String reason) {
        saga.setStatus(SagaStatus.FAILED);
        saga.setFailureReason(reason);
        saga.setCompletedAt(LocalDateTime.now());
        transactionRepository.save(saga);
    }

    @Transactional
    public void compensate(SagaTransaction saga, int failedStepIndex) {
        log.info("Starting compensation for saga {} from step {}", saga.getId(), failedStepIndex);

        saga.setStatus(SagaStatus.COMPENSATING);
        transactionRepository.save(saga);

        List<SagaStep> steps = saga.getSteps();

        for (int i = failedStepIndex - 1; i >= 0; i--) {
            SagaStep step = steps.get(i);

            if (step.getStatus() != StepStatus.COMPLETED) {
                continue;
            }

            StepHandler handler = stepHandlers.get(step.getStepName());
            if (handler == null) {
                log.warn("No handler for compensation of step: {}", step.getStepName());
                continue;
            }

            step.setStatus(StepStatus.COMPENSATING);
            stepRepository.save(step);

            try {
                handler.compensate(step);
                step.setStatus(StepStatus.COMPENSATED);
                step.setCompensatedAt(LocalDateTime.now());
                stepRepository.save(step);
                log.info("Step {} compensated for saga {}", step.getStepName(), saga.getId());

            } catch (Exception e) {
                log.error("Compensation failed for step {}: {}", step.getStepName(), e.getMessage());
                step.setErrorMessage("Compensation failed: " + e.getMessage());
                stepRepository.save(step);
            }
        }

        saga.setStatus(SagaStatus.COMPENSATED);
        transactionRepository.save(saga);
        log.info("SAGA {} compensation completed", saga.getId());
    }

    private String getRandomProduct() {
        String[] products = {"Laptop", "Smartphone", "Tablet", "Headphones", "Monitor", "Keyboard", "Mouse", "Webcam"};
        return products[RANDOM.nextInt(products.length)];
    }
}
