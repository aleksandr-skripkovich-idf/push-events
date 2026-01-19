package com.test.saga.step;

import com.test.saga.model.SagaStep;
import com.test.saga.model.StepName;

public interface StepHandler {

    StepName getStepName();

    void execute(SagaStep step) throws StepExecutionException;

    void compensate(SagaStep step);
}
