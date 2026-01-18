package com.test.saga.controller;

import com.test.saga.model.SagaStatus;
import com.test.saga.model.SagaTransaction;
import com.test.saga.service.SagaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/saga")
public class SagaController {

    private final SagaService sagaService;

    public SagaController(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startSaga() {
        SagaTransaction saga = sagaService.startNewSaga();
        return ResponseEntity.ok(Map.of(
                "sagaId", saga.getId(),
                "status", saga.getStatus(),
                "message", "Saga started asynchronously"
        ));
    }

    @GetMapping
    public ResponseEntity<Page<SagaTransaction>> getSagas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) SagaStatus status,
            @PageableDefault(size = 20) Pageable pageable) {

        Page<SagaTransaction> sagas = sagaService.getSagas(startDate, endDate, status, pageable);
        return ResponseEntity.ok(sagas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SagaTransaction> getSagaById(@PathVariable UUID id) {
        SagaTransaction saga = sagaService.getSagaById(id);
        if (saga == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(saga);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(sagaService.getStats());
    }
}
