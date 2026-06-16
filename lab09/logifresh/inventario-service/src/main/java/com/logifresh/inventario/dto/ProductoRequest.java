package com.logifresh.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ProductoRequest {

    @NotBlank(message = "El codigo es obligatorio")
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Min(value = 0, message = "El stock minimo no puede ser negativo")
    private Integer stockMinimo;

    public ProductoRequest() {
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

}
