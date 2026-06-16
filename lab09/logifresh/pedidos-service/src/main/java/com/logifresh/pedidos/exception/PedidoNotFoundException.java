package com.logifresh.pedidos.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Long id) {
        super("Pedido no encontrado con id: " + id);
    }

}
