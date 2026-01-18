package com.test.generator.service.service;

import com.test.generator.service.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatsService {

    private final EventRepository repository;

    public StatsService(EventRepository repository) {
        this.repository = repository;
    }

    public Map<String, Long> getStats() {
        return Map.of(
                "total", repository.count(),
                "processed", repository.countByProcessedTrue(),
                "unprocessed", repository.countByProcessedFalse()
        );
    }
}
