package com.logifresh.pedidos.dto;

public class InventarioStockRequest {

    private Integer cantidad;

    public InventarioStockRequest() {
    }

    public InventarioStockRequest(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

}
