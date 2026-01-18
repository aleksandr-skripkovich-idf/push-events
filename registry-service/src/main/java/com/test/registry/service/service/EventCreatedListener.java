package com.test.registry.service.service;

import com.test.push.events.api.model.EventCreatedMessage;
import com.test.registry.service.batch.EventQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(EventCreatedListener.class);
    private final EventQueue eventQueue;

    public EventCreatedListener(EventQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    @KafkaListener(
            topics = "${spring.kafka.topics.events-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(EventCreatedMessage message) {
        log.info("Received event, adding to queue: eventId={}, type={}, source={}", 
                message.getEventId(), message.getEventType(), message.getSourceService());

        eventQueue.add(message);

        log.debug("Queue size: {}", eventQueue.size());
    }
}
