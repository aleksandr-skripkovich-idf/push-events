package com.test.generator.service.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "events",
        indexes = {
        @Index(name = "idx_events_type", columnList = "type"),
        @Index(name = "idx_events_created_at", columnList = "createdAt"),
})
public class Event {

    public Event() {
    }

    public Event(UUID id, String type, String sourceService, LocalDateTime createdAt, boolean processed, String payload) {
        this.id = id;
        this.type = type;
        this.sourceService = sourceService;
        this.createdAt = createdAt;
        this.processed = processed;
        this.payload = payload;
    }

    @Id
    private UUID id;

    private String type;

    private String sourceService;

    private LocalDateTime createdAt;

    private boolean processed;

    @Column(length = 1000)
    private String payload;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
