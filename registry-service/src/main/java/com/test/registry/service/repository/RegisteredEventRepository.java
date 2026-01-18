package com.test.registry.service.repository;

import com.test.registry.service.model.RegisteredEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RegisteredEventRepository extends JpaRepository<RegisteredEvent, UUID>, 
        JpaSpecificationExecutor<RegisteredEvent> {
}
