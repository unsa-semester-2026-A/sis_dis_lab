package com.logifresh.notificaciones.controller;

import com.logifresh.notificaciones.dto.NotificacionRequest;
import com.logifresh.notificaciones.dto.NotificacionResponse;
import com.logifresh.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NotificacionResponse crear(@Valid @RequestBody NotificacionRequest request) {
        return service.crear(request);
    }

    @GetMapping("/{id}")
    public NotificacionResponse obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping
    public List<NotificacionResponse> listarTodas() {
        return service.listarTodas();
    }

}
