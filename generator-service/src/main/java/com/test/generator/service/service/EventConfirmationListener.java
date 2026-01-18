package com.test.generator.service.service;

import com.test.generator.service.model.EventConfirmedMessage;
import com.test.generator.service.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EventConfirmationListener {

    private static final Logger log = LoggerFactory.getLogger(EventConfirmationListener.class);
    private final EventRepository repository;

    public EventConfirmationListener(EventRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.events-confirmed}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handle(EventConfirmedMessage message) {
        repository.findById(message.getEventId())
                .ifPresent(event -> {
                    event.setProcessed(true);
                    repository.save(event);
                    log.info("Event confirmed {}", event.getId());
                });
    }
}
