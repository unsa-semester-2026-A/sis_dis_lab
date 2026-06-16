package com.logifresh.transporte.dto;

import com.logifresh.transporte.model.EstadoTransporte;
import com.logifresh.transporte.model.Transporte;
import java.time.LocalDateTime;

public class TransporteResponse {

    private Long id;
    private Long pedidoId;
    private String conductor;
    private String vehiculo;
    private EstadoTransporte estado;
    private LocalDateTime fechaAsignacion;

    public TransporteResponse() {
    }

    public static TransporteResponse fromEntity(Transporte transporte) {
        TransporteResponse response = new TransporteResponse();
        response.setId(transporte.getId());
        response.setPedidoId(transporte.getPedidoId());
        response.setConductor(transporte.getConductor());
        response.setVehiculo(transporte.getVehiculo());
        response.setEstado(transporte.getEstado());
        response.setFechaAsignacion(transporte.getFechaAsignacion());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public EstadoTransporte getEstado() {
        return estado;
    }

    public void setEstado(EstadoTransporte estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

}
