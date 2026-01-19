package com.test.saga.model;

public enum SagaStatus {
    STARTED("started"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    FAILED("failed"),
    COMPENSATING("compensating"),
    COMPENSATED("compensated");

    private final String key;

    SagaStatus(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
