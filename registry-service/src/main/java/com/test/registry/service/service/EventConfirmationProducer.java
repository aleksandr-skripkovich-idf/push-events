package com.test.registry.service.service;

import com.test.push.events.api.model.EventConfirmedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class EventConfirmationProducer {

    private static final Logger log = LoggerFactory.getLogger(EventConfirmationProducer.class);
    private final KafkaTemplate<String, EventConfirmedMessage> kafkaTemplate;

    @Value("${spring.kafka.topics.events-confirmed}")
    private String eventsConfirmedTopic;

    public EventConfirmationProducer(KafkaTemplate<String, EventConfirmedMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendConfirmation(UUID eventId) {
        log.info("Sending confirmation for event {} to Kafka", eventId);

        EventConfirmedMessage confirmation = new EventConfirmedMessage(eventId, LocalDateTime.now());

        kafkaTemplate.send(
                eventsConfirmedTopic,
                eventId.toString(),
                confirmation
        );
    }
}
