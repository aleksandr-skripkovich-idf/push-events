package com.test.saga.step;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.saga.model.Inventory;
import com.test.saga.model.Order;
import com.test.saga.model.SagaStep;
import com.test.saga.repository.InventoryRepository;
import com.test.saga.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class InventoryStepHandler implements StepHandler {

    private static final Logger log = LoggerFactory.getLogger(InventoryStepHandler.class);
    private static final Random RANDOM = new Random();

    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    @Value("${saga.failure.probability:0.3}")
    private double failureProbability;

    public InventoryStepHandler(InventoryRepository inventoryRepository,
                                OrderRepository orderRepository,
                                ObjectMapper objectMapper) {
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getStepName() {
        return "RESERVE_INVENTORY";
    }

    @Override
    public void execute(SagaStep step) throws StepExecutionException {
        log.info("Executing INVENTORY step for saga: {}", step.getTransaction().getId());

        if (RANDOM.nextDouble() < failureProbability) {
            log.error("INVENTORY step failed randomly for saga: {}", step.getTransaction().getId());
            throw new StepExecutionException("Random failure in INVENTORY step: Insufficient stock");
        }

        try {
            JsonNode input = objectMapper.readTree(step.getInputData());

            Order order = orderRepository.findBySagaId(step.getTransaction().getId())
                    .orElseThrow(() -> new StepExecutionException("Order not found for saga"));

            Inventory reservation = new Inventory(
                    UUID.randomUUID(),
                    step.getTransaction().getId(),
                    order.getId(),
                    input.get("productName").asText(),
                    input.get("quantity").asInt()
            );

            inventoryRepository.save(reservation);

            String output = objectMapper.writeValueAsString(
                    new InventoryOutput(reservation.getId(), reservation.getStatus().name())
            );
            step.setOutputData(output);

            log.info("INVENTORY step completed for saga: {}, reservationId: {}", 
                    step.getTransaction().getId(), reservation.getId());

        } catch (StepExecutionException e) {
            throw e;
        } catch (Exception e) {
            throw new StepExecutionException("Failed to reserve inventory: " + e.getMessage(), e);
        }
    }

    @Override
    public void compensate(SagaStep step) {
        log.info("Compensating INVENTORY step for saga: {}", step.getTransaction().getId());

        inventoryRepository.findBySagaId(step.getTransaction().getId())
                .ifPresent(reservation -> {
                    reservation.setStatus(Inventory.ReservationStatus.RELEASED);
                    reservation.setReleasedAt(LocalDateTime.now());
                    inventoryRepository.save(reservation);
                    log.info("Inventory reservation released: {}", reservation.getId());
                });
    }

    private record InventoryOutput(UUID reservationId, String status) {}
}
