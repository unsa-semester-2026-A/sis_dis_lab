package com.logifresh.notificaciones.controller;

import com.logifresh.notificaciones.service.StatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final StatusService statusService;

    public HealthController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return statusService.getHealth();
    }

    @GetMapping("/api/status")
    public Map<String, String> status() {
        return statusService.getStatus();
    }

}
