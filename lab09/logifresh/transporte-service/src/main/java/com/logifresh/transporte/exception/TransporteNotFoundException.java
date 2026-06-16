package com.logifresh.transporte.exception;

public class TransporteNotFoundException extends RuntimeException {

    public TransporteNotFoundException(Long id) {
        super("Transporte no encontrado con id: " + id);
    }

}
