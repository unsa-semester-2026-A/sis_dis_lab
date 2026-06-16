package com.logifresh.pedidos.exception;

public class OrquestacionException extends RuntimeException {

    public OrquestacionException(String message) {
        super("Error en la orquestacion del pedido: " + message);
    }

    public OrquestacionException(String message, Throwable cause) {
        super("Error en la orquestacion del pedido: " + message, cause);
    }

}
