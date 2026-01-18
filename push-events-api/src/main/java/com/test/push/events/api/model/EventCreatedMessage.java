package com.test.push.events.api.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EventCreatedMessage {

    private UUID eventId;
    private EventType eventType;
    private String sourceService;
    private LocalDateTime createdAt;
    private String payload;

    public EventCreatedMessage() {}

    public EventCreatedMessage(UUID eventId, EventType eventType, String sourceService,
                               LocalDateTime createdAt, String payload) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.sourceService = sourceService;
        this.createdAt = createdAt;
        this.payload = payload;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
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

    @Override
    public String toString() {
        return "EventCreatedMessage{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", sourceService='" + sourceService + '\'' +
                ", createdAt=" + createdAt +
                ", payload='" + payload + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventCreatedMessage)) return false;
        EventCreatedMessage that = (EventCreatedMessage) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(eventType, that.eventType) &&
                Objects.equals(sourceService, that.sourceService) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(payload, that.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, sourceService, createdAt, payload);
    }
}
