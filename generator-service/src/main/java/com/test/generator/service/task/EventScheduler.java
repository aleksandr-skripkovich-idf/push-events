package com.test.generator.service.task;

import com.test.generator.service.model.Event;
import com.test.generator.service.repository.EventRepository;
import com.test.generator.service.service.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EventScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);
    private final EventRepository repository;
    private final EventProducer producer;

    @Value("${spring.application.name}")
    private String serviceName;

    public EventScheduler(EventRepository repository,
                          EventProducer producer
    ) {
        this.repository = repository;
        this.producer = producer;
    }

    @Scheduled(fixedDelayString = "${event.generation.delay:5000}")
    @Transactional
    public void generate() {
        Event event = new Event(
                UUID.randomUUID(),
                "TEST_EVENT",
                serviceName,
                LocalDateTime.now(),
                false,
                "payload"
        );

        repository.save(event);
        producer.send(event);

        log.info("Generated event {}", event.getId());
    }
}
