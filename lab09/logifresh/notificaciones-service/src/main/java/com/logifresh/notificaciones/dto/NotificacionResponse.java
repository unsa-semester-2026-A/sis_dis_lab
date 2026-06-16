package com.logifresh.notificaciones.dto;

import com.logifresh.notificaciones.model.EstadoNotificacion;
import com.logifresh.notificaciones.model.Notificacion;
import java.time.LocalDateTime;

public class NotificacionResponse {

    private Long id;
    private String destinatario;
    private String asunto;
    private String mensaje;
    private EstadoNotificacion estado;
    private LocalDateTime fechaEnvio;

    public NotificacionResponse() {
    }

    public static NotificacionResponse fromEntity(Notificacion notificacion) {
        NotificacionResponse response = new NotificacionResponse();
        response.setId(notificacion.getId());
        response.setDestinatario(notificacion.getDestinatario());
        response.setAsunto(notificacion.getAsunto());
        response.setMensaje(notificacion.getMensaje());
        response.setEstado(notificacion.getEstado());
        response.setFechaEnvio(notificacion.getFechaEnvio());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public EstadoNotificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoNotificacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

}
