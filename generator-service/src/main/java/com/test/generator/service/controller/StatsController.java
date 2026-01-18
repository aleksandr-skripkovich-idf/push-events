package com.test.generator.service.controller;

import com.test.generator.service.repository.EventRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final EventRepository repository;

    public StatsController(EventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Map<String, Long> stats() {
        return Map.of(
                "total", repository.count(),
                "processed", repository.countByProcessedTrue(),
                "unprocessed", repository.countByProcessedFalse()
        );
    }
}
