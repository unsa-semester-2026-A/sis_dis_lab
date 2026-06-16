package com.logifresh.transporte.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "transportes")
public class Transporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pedidoId;

    @Column(nullable = false)
    private String conductor;

    @Column(nullable = false)
    private String vehiculo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTransporte estado;

    @Column(nullable = false)
    private LocalDateTime fechaAsignacion;

    public Transporte() {
    }

    public Transporte(Long pedidoId, String conductor, String vehiculo) {
        this.pedidoId = pedidoId;
        this.conductor = conductor;
        this.vehiculo = vehiculo;
        this.estado = EstadoTransporte.ASIGNADO;
        this.fechaAsignacion = LocalDateTime.now();
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
