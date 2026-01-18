package com.test.generator.service.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class EventConfirmedMessage {
    private UUID eventId;
    private LocalDateTime confirmedAt;

    public EventConfirmedMessage() {
    }

    public EventConfirmedMessage(UUID eventId, LocalDateTime confirmedAt) {
        this.eventId = eventId;
        this.confirmedAt = confirmedAt;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    @Override
    public String toString() {
        return "EventConfirmedMessage{" +
                "eventId=" + eventId +
                ", confirmedAt=" + confirmedAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventConfirmedMessage that)) return false;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(confirmedAt, that.confirmedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, confirmedAt);
    }
}
