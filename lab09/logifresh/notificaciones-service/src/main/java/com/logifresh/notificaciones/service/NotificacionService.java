package com.logifresh.notificaciones.service;

import com.logifresh.notificaciones.dto.NotificacionRequest;
import com.logifresh.notificaciones.dto.NotificacionResponse;
import com.logifresh.notificaciones.exception.NotificacionNotFoundException;
import com.logifresh.notificaciones.model.Notificacion;
import com.logifresh.notificaciones.repository.NotificacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificacionService {

    private final NotificacionRepository repository;

    public NotificacionService(NotificacionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public NotificacionResponse crear(NotificacionRequest request) {
        Notificacion notificacion = new Notificacion(
                request.getDestinatario(),
                request.getAsunto(),
                request.getMensaje()
        );
        return NotificacionResponse.fromEntity(repository.save(notificacion));
    }

    @Transactional(readOnly = true)
    public NotificacionResponse obtenerPorId(Long id) {
        Notificacion notificacion = repository.findById(id)
                .orElseThrow(() -> new NotificacionNotFoundException(id));
        return NotificacionResponse.fromEntity(notificacion);
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarTodas() {
        return repository.findAll().stream()
                .map(NotificacionResponse::fromEntity)
                .toList();
    }

}
