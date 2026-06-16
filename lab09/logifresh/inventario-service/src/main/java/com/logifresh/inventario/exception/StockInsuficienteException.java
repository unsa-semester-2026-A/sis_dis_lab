package com.logifresh.inventario.exception;

public class StockInsuficienteException extends RuntimeException {

    public StockInsuficienteException(Long productoId, Integer disponible, Integer solicitado) {
        super("Stock insuficiente para el producto " + productoId
                + ". Disponible: " + disponible + ", solicitado: " + solicitado);
    }

}
