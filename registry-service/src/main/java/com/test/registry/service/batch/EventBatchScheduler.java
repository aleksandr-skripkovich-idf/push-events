package com.test.registry.service.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EventBatchScheduler {

    private static final Logger log = LoggerFactory.getLogger(EventBatchScheduler.class);

    private final JobLauncher jobLauncher;
    private final Job processEventsJob;
    private final EventQueue eventQueue;

    public EventBatchScheduler(JobLauncher jobLauncher, Job processEventsJob, EventQueue eventQueue) {
        this.jobLauncher = jobLauncher;
        this.processEventsJob = processEventsJob;
        this.eventQueue = eventQueue;
    }

    @Scheduled(fixedDelayString = "${batch.processing.delay:10000}")
    public void runBatchJob() {
        if (eventQueue.isEmpty()) {
            log.debug("Queue is empty, skipping batch job");
            return;
        }

        try {
            int queueSize = eventQueue.size();
            log.info("Starting batch job, queue size: {}", queueSize);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(processEventsJob, jobParameters);

            log.info("Batch job completed successfully");
        } catch (Exception e) {
            log.error("Error running batch job", e);
        }
    }
}
