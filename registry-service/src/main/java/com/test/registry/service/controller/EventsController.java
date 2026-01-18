package com.test.registry.service.controller;

import com.test.push.events.api.model.EventType;
import com.test.registry.service.model.RegisteredEvent;
import com.test.registry.service.service.EventsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/events")
public class EventsController {

    private final EventsService eventsService;

    public EventsController(EventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping
    public ResponseEntity<Page<RegisteredEvent>> getEvents(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) EventType eventType,
            @RequestParam(required = false) String sourceService,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<RegisteredEvent> events = eventsService.getEvents(startDate, endDate, eventType, sourceService, pageable);
        return ResponseEntity.ok(events);
    }
}
