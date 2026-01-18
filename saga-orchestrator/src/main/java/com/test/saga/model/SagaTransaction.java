package com.test.saga.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "saga_transactions", indexes = {
        @Index(name = "idx_saga_tx_status", columnList = "status"),
        @Index(name = "idx_saga_tx_started_at", columnList = "startedAt"),
        @Index(name = "idx_saga_tx_status_started", columnList = "status, startedAt")
})
public class SagaTransaction {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SagaStatus status;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private String failureReason;

    private int currentStep;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @OrderBy("stepOrder ASC")
    private List<SagaStep> steps = new ArrayList<>();

    public SagaTransaction() {
    }

    public SagaTransaction(UUID id) {
        this.id = id;
        this.status = SagaStatus.STARTED;
        this.startedAt = LocalDateTime.now();
        this.currentStep = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SagaStatus getStatus() {
        return status;
    }

    public void setStatus(SagaStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    public List<SagaStep> getSteps() {
        return steps;
    }

    public void setSteps(List<SagaStep> steps) {
        this.steps = steps;
    }

    public void addStep(SagaStep step) {
        steps.add(step);
        step.setTransaction(this);
    }
}
