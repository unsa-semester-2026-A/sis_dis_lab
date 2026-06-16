package com.logifresh.inventario.dto;

import com.logifresh.inventario.model.Producto;
import java.time.LocalDateTime;

public class ProductoResponse {

    private Long id;
    private String codigo;
    private String nombre;
    private Integer stock;
    private Integer stockMinimo;
    private LocalDateTime fechaCreacion;

    public ProductoResponse() {
    }

    public static ProductoResponse fromEntity(Producto producto) {
        ProductoResponse response = new ProductoResponse();
        response.setId(producto.getId());
        response.setCodigo(producto.getCodigo());
        response.setNombre(producto.getNombre());
        response.setStock(producto.getStock());
        response.setStockMinimo(producto.getStockMinimo());
        response.setFechaCreacion(producto.getFechaCreacion());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

}
