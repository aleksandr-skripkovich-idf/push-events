package com.test.generator.service.task;

import com.test.generator.service.model.Event;
import com.test.generator.service.repository.EventRepository;
import com.test.generator.service.service.EventProducer;
import com.test.push.events.api.model.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class EventScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventScheduler.class);
    private static final Random RANDOM = new Random();
    private static final EventType[] EVENT_TYPES = EventType.values();

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

    @Scheduled(fixedDelayString = "${event.generation.delay:10000}")
    @Transactional
    public void generate() {
        EventType eventType = getRandomEventType();
        String payload = generatePayload(eventType);

        Event event = new Event(
                UUID.randomUUID(),
                eventType.name(),
                serviceName,
                LocalDateTime.now(),
                false,
                payload
        );

        repository.save(event);
        producer.send(event);

        log.info("Generated event: id={}, type={}, payload={}", event.getId(), eventType, payload);
    }

    private EventType getRandomEventType() {
        return EVENT_TYPES[RANDOM.nextInt(EVENT_TYPES.length)];
    }

    private String generatePayload(EventType eventType) {
        int id = RANDOM.nextInt(10000);
        return switch (eventType) {
            case USER_CREATED -> String.format("{\"userId\": %d, \"username\": \"user_%d\", \"email\": \"user%d@example.com\"}", id, id, id);
            case USER_UPDATED -> String.format("{\"userId\": %d, \"field\": \"email\", \"newValue\": \"updated%d@example.com\"}", id, id);
            case ORDER_PLACED -> String.format("{\"orderId\": %d, \"userId\": %d, \"amount\": %.2f, \"currency\": \"USD\"}", id, RANDOM.nextInt(1000), RANDOM.nextDouble() * 500);
            case ORDER_COMPLETED -> String.format("{\"orderId\": %d, \"status\": \"DELIVERED\", \"deliveryTime\": \"%s\"}", id, LocalDateTime.now().minusHours(RANDOM.nextInt(48)));
            case PAYMENT_RECEIVED -> String.format("{\"paymentId\": %d, \"orderId\": %d, \"amount\": %.2f, \"method\": \"%s\"}", id, RANDOM.nextInt(10000), RANDOM.nextDouble() * 1000, getRandomPaymentMethod());
            case NOTIFICATION_SENT -> String.format("{\"notificationId\": %d, \"userId\": %d, \"channel\": \"%s\", \"template\": \"%s\"}", id, RANDOM.nextInt(1000), getRandomChannel(), getRandomTemplate());
            case SYSTEM_ALERT -> String.format("{\"alertId\": %d, \"severity\": \"%s\", \"message\": \"%s\", \"component\": \"%s\"}", id, getRandomSeverity(), getRandomAlertMessage(), serviceName);
        };
    }

    private String getRandomPaymentMethod() {
        String[] methods = {"CARD", "PAYPAL", "BANK_TRANSFER", "CRYPTO"};
        return methods[RANDOM.nextInt(methods.length)];
    }

    private String getRandomChannel() {
        String[] channels = {"EMAIL", "SMS", "PUSH", "TELEGRAM"};
        return channels[RANDOM.nextInt(channels.length)];
    }

    private String getRandomTemplate() {
        String[] templates = {"welcome", "order_confirmation", "password_reset", "promo_offer"};
        return templates[RANDOM.nextInt(templates.length)];
    }

    private String getRandomSeverity() {
        String[] severities = {"INFO", "WARNING", "ERROR", "CRITICAL"};
        return severities[RANDOM.nextInt(severities.length)];
    }

    private String getRandomAlertMessage() {
        String[] messages = {"High CPU usage detected", "Memory threshold exceeded", "Service response time degraded", "Database connection pool exhausted", "Disk space running low"};
        return messages[RANDOM.nextInt(messages.length)];
    }
}
