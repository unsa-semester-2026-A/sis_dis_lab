package com.logifresh.transporte.dto;

import jakarta.validation.constraints.NotBlank;

public class TransporteEstadoRequest {

    @NotBlank(message = "El estado es obligatorio")
    private String estado;

    public TransporteEstadoRequest() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
