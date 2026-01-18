package com.test.saga.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "saga_steps", indexes = {
        @Index(name = "idx_saga_steps_transaction_id", columnList = "transaction_id")
})
public class SagaStep {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    @JsonIgnore
    private SagaTransaction transaction;

    private int stepOrder;

    private String stepName;

    @Enumerated(EnumType.STRING)
    private StepStatus status;

    private LocalDateTime executedAt;

    private LocalDateTime compensatedAt;

    private String errorMessage;

    @Column(length = 2000)
    private String inputData;

    @Column(length = 2000)
    private String outputData;

    public SagaStep() {
    }

    public SagaStep(UUID id, int stepOrder, String stepName, String inputData) {
        this.id = id;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.inputData = inputData;
        this.status = StepStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SagaTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(SagaTransaction transaction) {
        this.transaction = transaction;
    }

    public int getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public StepStatus getStatus() {
        return status;
    }

    public void setStatus(StepStatus status) {
        this.status = status;
    }

    public LocalDateTime getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }

    public LocalDateTime getCompensatedAt() {
        return compensatedAt;
    }

    public void setCompensatedAt(LocalDateTime compensatedAt) {
        this.compensatedAt = compensatedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }
}
