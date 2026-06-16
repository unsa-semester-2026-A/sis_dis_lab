package com.logifresh.inventario.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatusService {

    private final String serviceName;

    public StatusService(@Value("${spring.application.name}") String serviceName) {
        this.serviceName = serviceName;
    }

    public Map<String, String> getHealth() {
        return Map.of(
                "status", "UP",
                "service", serviceName
        );
    }

    public Map<String, String> getStatus() {
        return Map.of(
                "service", serviceName,
                "message", "Servicio operativo"
        );
    }

}
