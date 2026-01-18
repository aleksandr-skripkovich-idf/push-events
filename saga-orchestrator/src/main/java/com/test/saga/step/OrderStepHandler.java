package com.test.saga.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.saga.model.Order;
import com.test.saga.model.SagaStep;
import com.test.saga.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class OrderStepHandler implements StepHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderStepHandler.class);
    private static final Random RANDOM = new Random();

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Value("${saga.failure.probability:0.3}")
    private double failureProbability;

    public OrderStepHandler(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getStepName() {
        return "CREATE_ORDER";
    }

    @Override
    public void execute(SagaStep step) throws StepExecutionException {
        log.info("Executing ORDER step for saga: {}", step.getTransaction().getId());

        if (RANDOM.nextDouble() < failureProbability) {
            log.error("ORDER step failed randomly for saga: {}", step.getTransaction().getId());
            throw new StepExecutionException("Random failure in ORDER step: Database connection timeout");
        }

        try {
            JsonNode input = objectMapper.readTree(step.getInputData());

            Order order = new Order(
                    UUID.randomUUID(),
                    step.getTransaction().getId(),
                    UUID.fromString(input.get("customerId").asText()),
                    input.get("productName").asText(),
                    input.get("quantity").asInt(),
                    new BigDecimal(input.get("amount").asText())
            );

            orderRepository.save(order);

            String output = objectMapper.writeValueAsString(
                    new OrderOutput(order.getId(), order.getStatus().name())
            );
            step.setOutputData(output);

            log.info("ORDER step completed for saga: {}, orderId: {}", step.getTransaction().getId(), order.getId());

        } catch (Exception e) {
            throw new StepExecutionException("Failed to create order: " + e.getMessage(), e);
        }
    }

    @Override
    public void compensate(SagaStep step) {
        log.info("Compensating ORDER step for saga: {}", step.getTransaction().getId());

        orderRepository.findBySagaId(step.getTransaction().getId())
                .ifPresent(order -> {
                    order.setStatus(Order.OrderStatus.CANCELLED);
                    order.setCancelledAt(LocalDateTime.now());
                    orderRepository.save(order);
                    log.info("Order cancelled: {}", order.getId());
                });
    }

    private record OrderOutput(UUID orderId, String status) {}
}
