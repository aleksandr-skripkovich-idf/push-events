package com.test.saga.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.saga.model.Order;
import com.test.saga.model.Payment;
import com.test.saga.model.SagaStep;
import com.test.saga.repository.OrderRepository;
import com.test.saga.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class PaymentStepHandler implements StepHandler {

    private static final Logger log = LoggerFactory.getLogger(PaymentStepHandler.class);
    private static final Random RANDOM = new Random();
    private static final String[] PAYMENT_METHODS = {"CARD", "PAYPAL", "BANK_TRANSFER"};

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Value("${saga.failure.probability:0.3}")
    private double failureProbability;

    public PaymentStepHandler(PaymentRepository paymentRepository,
                             OrderRepository orderRepository,
                             ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getStepName() {
        return "PROCESS_PAYMENT";
    }

    @Override
    public void execute(SagaStep step) throws StepExecutionException {
        log.info("Executing PAYMENT step for saga: {}", step.getTransaction().getId());

        if (RANDOM.nextDouble() < failureProbability) {
            log.error("PAYMENT step failed randomly for saga: {}", step.getTransaction().getId());
            throw new StepExecutionException("Random failure in PAYMENT step: Payment declined by bank");
        }

        try {
            JsonNode input = objectMapper.readTree(step.getInputData());

            Order order = orderRepository.findBySagaId(step.getTransaction().getId())
                    .orElseThrow(() -> new StepExecutionException("Order not found for saga"));

            Payment payment = new Payment(
                    UUID.randomUUID(),
                    step.getTransaction().getId(),
                    order.getId(),
                    UUID.fromString(input.get("customerId").asText()),
                    new BigDecimal(input.get("amount").asText()),
                    PAYMENT_METHODS[RANDOM.nextInt(PAYMENT_METHODS.length)]
            );

            paymentRepository.save(payment);

            order.setStatus(Order.OrderStatus.CONFIRMED);
            orderRepository.save(order);

            String output = objectMapper.writeValueAsString(
                    new PaymentOutput(payment.getId(), payment.getStatus().name(), payment.getPaymentMethod())
            );
            step.setOutputData(output);

            log.info("PAYMENT step completed for saga: {}, paymentId: {}", 
                    step.getTransaction().getId(), payment.getId());

        } catch (StepExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new StepExecutionException("Failed to process payment: " + e.getMessage(), e);
        }
    }

    @Override
    public void compensate(SagaStep step) {
        log.info("Compensating PAYMENT step for saga: {}", step.getTransaction().getId());

        paymentRepository.findBySagaId(step.getTransaction().getId())
                .ifPresent(payment -> {
                    payment.setStatus(Payment.PaymentStatus.REFUNDED);
                    payment.setRefundedAt(LocalDateTime.now());
                    paymentRepository.save(payment);
                    log.info("Payment refunded: {}", payment.getId());
                });
    }

    private record PaymentOutput(UUID paymentId, String status, String method) {}
}
