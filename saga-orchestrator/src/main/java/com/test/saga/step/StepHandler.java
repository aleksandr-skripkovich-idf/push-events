package com.test.saga.step;

import com.test.saga.model.SagaStep;

public interface StepHandler {

    String getStepName();

    void execute(SagaStep step) throws StepExecutionException;

    void compensate(SagaStep step);
}
