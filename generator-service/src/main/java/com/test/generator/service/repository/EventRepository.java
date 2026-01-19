package com.test.generator.service.repository;

import com.test.generator.service.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    long countByProcessedTrue();
    long countByProcessedFalse();
}
