package com.logifresh.notificaciones.exception;

public class NotificacionNotFoundException extends RuntimeException {

    public NotificacionNotFoundException(Long id) {
        super("Notificacion no encontrada con id: " + id);
    }

}
