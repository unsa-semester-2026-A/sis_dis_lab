package com.logifresh.pedidos.dto;

import com.logifresh.pedidos.model.EstadoPedido;
import com.logifresh.pedidos.model.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PedidoResponse {

    private Long id;
    private String cliente;
    private Long productoId;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal;
    private BigDecimal total;
    private String estado;
    private LocalDateTime fechaCreacion;

    public PedidoResponse() {
    }

    public static PedidoResponse fromEntity(Pedido pedido) {
        PedidoResponse response = new PedidoResponse();
        response.setId(pedido.getId());
        response.setCliente(pedido.getCliente());
        response.setProductoId(pedido.getProductoId());
        response.setCantidad(pedido.getCantidad());
        response.setPrecioUnitario(pedido.getPrecioUnitario());
        response.setDescuento(pedido.getDescuento());
        response.setSubtotal(pedido.getSubtotal());
        response.setTotal(pedido.getTotal());
        response.setEstado(pedido.getEstado().name());
        response.setFechaCreacion(pedido.getFechaCreacion());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
