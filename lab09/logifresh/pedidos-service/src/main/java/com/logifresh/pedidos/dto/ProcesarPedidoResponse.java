package com.logifresh.pedidos.dto;

public class ProcesarPedidoResponse {

    private Long pedidoId;
    private Long facturaId;
    private Long transporteId;
    private Long notificacionId;
    private String estado;

    public ProcesarPedidoResponse() {
    }

    public ProcesarPedidoResponse(Long pedidoId, Long facturaId, Long transporteId,
                                  Long notificacionId, String estado) {
        this.pedidoId = pedidoId;
        this.facturaId = facturaId;
        this.transporteId = transporteId;
        this.notificacionId = notificacionId;
        this.estado = estado;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public Long getFacturaId() {
        return facturaId;
    }

    public void setFacturaId(Long facturaId) {
        this.facturaId = facturaId;
    }

    public Long getTransporteId() {
        return transporteId;
    }

    public void setTransporteId(Long transporteId) {
        this.transporteId = transporteId;
    }

    public Long getNotificacionId() {
        return notificacionId;
    }

    public void setNotificacionId(Long notificacionId) {
        this.notificacionId = notificacionId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
