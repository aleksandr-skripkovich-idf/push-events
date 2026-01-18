package com.test.registry.service.service;

import com.test.push.events.api.model.EventCreatedMessage;
import com.test.registry.service.model.RegisteredEvent;
import com.test.registry.service.repository.RegisteredEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class EventCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(EventCreatedListener.class);
    private final RegisteredEventRepository repository;
    private final EventConfirmationProducer confirmationProducer;

    public EventCreatedListener(RegisteredEventRepository repository, 
                               EventConfirmationProducer confirmationProducer) {
        this.repository = repository;
        this.confirmationProducer = confirmationProducer;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.events-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handle(EventCreatedMessage message) {
        log.info("Received event created message: eventId={}, type={}, source={}", 
                message.getEventId(), message.getEventType(), message.getSourceService());

        RegisteredEvent registeredEvent = new RegisteredEvent(
                message.getEventId(),
                message.getEventType().name(),
                message.getSourceService(),
                message.getCreatedAt(),
                message.getPayload(),
                LocalDateTime.now()
        );

        repository.save(registeredEvent);
        log.info("Registered event in database: eventId={}", message.getEventId());

        confirmationProducer.sendConfirmation(message.getEventId());
        log.info("Sent confirmation for event: eventId={}", message.getEventId());
    }
}
