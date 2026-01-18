package com.test.saga.repository;

import com.test.saga.model.SagaStatus;
import com.test.saga.model.SagaTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SagaTransactionRepository extends JpaRepository<SagaTransaction, UUID>,
        JpaSpecificationExecutor<SagaTransaction> {

    long countByStatus(SagaStatus status);
}
