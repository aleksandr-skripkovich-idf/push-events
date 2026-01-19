package com.test.registry.service.configuration;

import com.test.push.events.api.model.EventCreatedMessage;
import com.test.registry.service.batch.EventQueue;
import com.test.registry.service.model.RegisteredEvent;
import com.test.registry.service.repository.RegisteredEventRepository;
import com.test.registry.service.service.EventConfirmationProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

@Configuration
public class EventBatchConfig {

    private static final Logger log = LoggerFactory.getLogger(EventBatchConfig.class);

    private final EventQueue eventQueue;
    private final RegisteredEventRepository repository;
    private final EventConfirmationProducer confirmationProducer;

    public EventBatchConfig(EventQueue eventQueue,
                           RegisteredEventRepository repository,
                           EventConfirmationProducer confirmationProducer) {
        this.eventQueue = eventQueue;
        this.repository = repository;
        this.confirmationProducer = confirmationProducer;
    }

    @Bean
    public Job processEventsJob(JobRepository jobRepository, Step processEventsStep) {
        return new JobBuilder("processEventsJob", jobRepository)
                .start(processEventsStep)
                .build();
    }

    @Bean
    public Step processEventsStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("processEventsStep", jobRepository)
                .<EventCreatedMessage, RegisteredEvent>chunk(10, transactionManager)
                .reader(eventItemReader())
                .processor(eventItemProcessor())
                .writer(eventItemWriter())
                .build();
    }

    private ItemReader<EventCreatedMessage> eventItemReader() {
        return new ItemReader<>() {
            private Iterator<EventCreatedMessage> iterator;

            @Override
            public EventCreatedMessage read() {
                if (iterator == null) {
                    List<EventCreatedMessage> messages = eventQueue.drainAll();
                    log.info("Batch reader: drained {} messages from queue", messages.size());
                    iterator = messages.iterator();
                }

                if (iterator.hasNext()) {
                    return iterator.next();
                } else {
                    iterator = null;
                    return null;
                }
            }
        };
    }

    private ItemProcessor<EventCreatedMessage, RegisteredEvent> eventItemProcessor() {
        return message -> {
            log.debug("Processing event: eventId={}, type={}", message.getEventId(), message.getEventType());

            return new RegisteredEvent(
                    message.getEventId(),
                    message.getEventType().name(),
                    message.getSourceService(),
                    message.getCreatedAt(),
                    message.getPayload(),
                    LocalDateTime.now()
            );
        };
    }

    private ItemWriter<RegisteredEvent> eventItemWriter() {
        return items -> {
            log.info("Batch writer: saving {} events to database", items.size());

            for (RegisteredEvent event : items) {
                repository.save(event);
                confirmationProducer.sendConfirmation(event.getEventId());
                log.debug("Saved and confirmed event: eventId={}", event.getEventId());
            }

            log.info("Batch writer: completed processing {} events", items.size());
        };
    }
}
