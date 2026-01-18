package com.test.saga.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations", indexes = {
        @Index(name = "idx_inventory_saga_id", columnList = "sagaId")
})
public class Inventory {

    @Id
    private UUID id;

    private UUID sagaId;

    private UUID orderId;

    private String productName;

    private int quantity;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime reservedAt;

    private LocalDateTime releasedAt;

    public Inventory() {
    }

    public Inventory(UUID id, UUID sagaId, UUID orderId, String productName, int quantity) {
        this.id = id;
        this.sagaId = sagaId;
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
        this.status = ReservationStatus.RESERVED;
        this.reservedAt = LocalDateTime.now();
    }

    public enum ReservationStatus {
        RESERVED, CONFIRMED, RELEASED
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSagaId() {
        return sagaId;
    }

    public void setSagaId(UUID sagaId) {
        this.sagaId = sagaId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public LocalDateTime getReservedAt() {
        return reservedAt;
    }

    public void setReservedAt(LocalDateTime reservedAt) {
        this.reservedAt = reservedAt;
    }

    public LocalDateTime getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(LocalDateTime releasedAt) {
        this.releasedAt = releasedAt;
    }
}
