package com.test.saga.repository;

import com.test.saga.model.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, UUID> {

    List<SagaStep> findByTransactionIdOrderByStepOrderAsc(UUID transactionId);

    List<SagaStep> findByTransactionIdOrderByStepOrderDesc(UUID transactionId);
}
