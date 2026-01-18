package com.test.registry.service.service;

import com.test.push.events.api.model.EventType;
import com.test.registry.service.model.RegisteredEvent;
import com.test.registry.service.repository.RegisteredEventRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventsService {

    private final RegisteredEventRepository repository;

    public EventsService(RegisteredEventRepository repository) {
        this.repository = repository;
    }

    public Page<RegisteredEvent> getEvents(LocalDateTime startDate, LocalDateTime endDate,
                                           EventType eventType, String sourceService, Pageable pageable) {
        Specification<RegisteredEvent> spec = buildSpecification(startDate, endDate, eventType, sourceService);
        return repository.findAll(spec, pageable);
    }

    private Specification<RegisteredEvent> buildSpecification(LocalDateTime startDate, LocalDateTime endDate,
                                                             EventType eventType, String sourceService) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            if (eventType != null) {
                predicates.add(criteriaBuilder.equal(root.get("eventType"), eventType));
            }
            if (sourceService != null && !sourceService.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("sourceService"), sourceService));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
