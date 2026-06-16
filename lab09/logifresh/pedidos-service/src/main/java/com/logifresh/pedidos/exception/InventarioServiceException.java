package com.logifresh.pedidos.exception;

public class InventarioServiceException extends RuntimeException {

    public InventarioServiceException(String message) {
        super("Error al comunicarse con inventario-service: " + message);
    }

}
