package com.test.saga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SagaOrchestratorApp {

    public static void main(String[] args) {
        SpringApplication.run(SagaOrchestratorApp.class, args);
    }
}
