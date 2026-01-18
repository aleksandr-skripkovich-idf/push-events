package com.test.generator.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.test.generator.service.model.Event;
import com.test.generator.service.model.EventCreatedMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
                        event.getType(),
                        event.getSourceService(),
                        event.getCreatedAt(),
                        event.getPayload()
                )
        );
    }
}
