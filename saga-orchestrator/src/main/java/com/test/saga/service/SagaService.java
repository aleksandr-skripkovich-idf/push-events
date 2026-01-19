package com.test.saga.service;

import com.test.saga.model.SagaStatus;
import com.test.saga.model.SagaTransaction;
import com.test.saga.repository.SagaTransactionRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class SagaService {

    private final SagaTransactionRepository transactionRepository;
    private final SagaOrchestrator orchestrator;

    public SagaService(SagaTransactionRepository transactionRepository, SagaOrchestrator orchestrator) {
        this.transactionRepository = transactionRepository;
        this.orchestrator = orchestrator;
    }

    public SagaTransaction startNewSaga() {
        UUID sagaId = UUID.randomUUID();
        orchestrator.startSaga(sagaId);

        SagaTransaction saga = new SagaTransaction(sagaId);
        return saga;
    }

    public Page<SagaTransaction> getSagas(LocalDateTime startDate, LocalDateTime endDate,
                                         SagaStatus status, Pageable pageable) {
        Specification<SagaTransaction> spec = buildSpecification(startDate, endDate, status);
        return transactionRepository.findAll(spec, pageable);
    }

    public SagaTransaction getSagaById(UUID id) {
        return transactionRepository.findById(id).orElse(null);
    }

    public Map<String, Long> getStats() {
        return Map.of(
                "total", transactionRepository.count(),
                SagaStatus.COMPLETED.getKey(), transactionRepository.countByStatus(SagaStatus.COMPLETED),
                SagaStatus.FAILED.getKey(), transactionRepository.countByStatus(SagaStatus.FAILED),
                SagaStatus.COMPENSATED.getKey(), transactionRepository.countByStatus(SagaStatus.COMPENSATED),
                SagaStatus.IN_PROGRESS.getKey(), transactionRepository.countByStatus(SagaStatus.IN_PROGRESS)
        );
    }

    private Specification<SagaTransaction> buildSpecification(LocalDateTime startDate, LocalDateTime endDate,
                                                              SagaStatus status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startedAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startedAt"), endDate));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
