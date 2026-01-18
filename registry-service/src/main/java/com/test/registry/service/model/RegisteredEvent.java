package com.test.registry.service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registered_events", indexes = {
        @Index(name = "idx_reg_events_registered_at", columnList = "registeredAt"),
        @Index(name = "idx_reg_events_event_type", columnList = "eventType"),
        @Index(name = "idx_reg_events_source_service", columnList = "sourceService")
})
public class RegisteredEvent {

    public RegisteredEvent() {
    }

    public RegisteredEvent(UUID eventId, String eventType, String sourceService, 
                          LocalDateTime createdAt, String payload, LocalDateTime registeredAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.createdAt = createdAt;
        this.payload = payload;
        this.registeredAt = registeredAt;
    }

    @Id
    private UUID eventId;

    private String eventType;

    private String sourceService;

    private LocalDateTime createdAt;

    @Column(length = 1000)
    private String payload;

    private LocalDateTime registeredAt;

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSourceService() {
        return sourceService;
    }

    public void setSourceService(String sourceService) {
        this.sourceService = sourceService;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }
}
