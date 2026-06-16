package com.logifresh.notificaciones.dto;

import jakarta.validation.constraints.NotBlank;

public class NotificacionRequest {

    @NotBlank(message = "El destinatario es obligatorio")
    private String destinatario;

    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;

    @NotBlank(message = "El mensaje es obligatorio")
    private String mensaje;

    public NotificacionRequest() {
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

}
