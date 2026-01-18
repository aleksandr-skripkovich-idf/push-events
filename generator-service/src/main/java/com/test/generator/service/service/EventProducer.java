package com.test.generator.service.service;

import com.test.generator.service.model.Event;
import com.test.push.events.api.model.EventCreatedMessage;
import com.test.push.events.api.model.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    private static final Logger log = LoggerFactory.getLogger(EventProducer.class);
    private final KafkaTemplate<String, EventCreatedMessage> kafkaTemplate;

    @Value("${spring.kafka.topics.events-created}")
    private String eventsCreatedTopic;

    public EventProducer(KafkaTemplate<String, EventCreatedMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Event event) {
        log.info("Sending event {} to Kafka", event.getId());

        kafkaTemplate.send(
                eventsCreatedTopic,
                event.getId().toString(),
                new EventCreatedMessage(
                        event.getId(),
                        EventType.valueOf(event.getType()),
                        event.getSourceService(),
                        event.getCreatedAt(),
                        event.getPayload()
                )
        );
    }
}
