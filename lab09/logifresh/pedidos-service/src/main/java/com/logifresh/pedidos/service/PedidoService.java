package com.logifresh.pedidos.service;

import com.logifresh.pedidos.dto.InventarioProductoResponse;
import com.logifresh.pedidos.dto.PedidoRequest;
import com.logifresh.pedidos.dto.PedidoResponse;
import com.logifresh.pedidos.exception.PedidoNotFoundException;
import com.logifresh.pedidos.exception.StockInsuficienteException;
import com.logifresh.pedidos.model.EstadoPedido;
import com.logifresh.pedidos.model.Pedido;
import com.logifresh.pedidos.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    private static final BigDecimal CINCO_PORCIENTO = BigDecimal.valueOf(5);
    private static final BigDecimal DIEZ_PORCIENTO = BigDecimal.valueOf(10);
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int UMBRAL_5 = 50;
    private static final int UMBRAL_10 = 100;

    private final PedidoRepository repository;
    private final InventarioClient inventarioClient;

    public PedidoService(PedidoRepository repository, InventarioClient inventarioClient) {
        this.repository = repository;
        this.inventarioClient = inventarioClient;
    }

    @Transactional
    public PedidoResponse registrar(PedidoRequest request) {
        BigDecimal descuento = calcularDescuento(request.getCantidad());
        BigDecimal cantidad = BigDecimal.valueOf(request.getCantidad());
        BigDecimal subtotal = request.getPrecioUnitario().multiply(cantidad);
        BigDecimal descuentoFactor = descuento.divide(CIEN, 4, RoundingMode.HALF_UP);
        BigDecimal descuentoAplicado = subtotal.multiply(descuentoFactor);
        BigDecimal total = subtotal.subtract(descuentoAplicado);

        InventarioProductoResponse producto = inventarioClient.obtenerProducto(
                request.getProductoId());

        if (producto.getStock() < request.getCantidad()) {
            throw new StockInsuficienteException(
                    request.getProductoId(), producto.getStock(), request.getCantidad());
        }

        inventarioClient.descontarStock(request.getProductoId(), request.getCantidad());

        Pedido pedido = new Pedido();
        pedido.setCliente(request.getCliente());
        pedido.setProductoId(request.getProductoId());
        pedido.setCantidad(request.getCantidad());
        pedido.setPrecioUnitario(request.getPrecioUnitario());
        pedido.setDescuento(descuento);
        pedido.setSubtotal(subtotal);
        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedido.setFechaCreacion(LocalDateTime.now());

        return PedidoResponse.fromEntity(repository.save(pedido));
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtenerPorId(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        return PedidoResponse.fromEntity(pedido);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(PedidoResponse::fromEntity)
                .toList();
    }

    @Transactional
    public PedidoResponse cancelar(Long id) {
        Pedido pedido = repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        pedido.setEstado(EstadoPedido.CANCELADO);
        return PedidoResponse.fromEntity(repository.save(pedido));
    }

    BigDecimal calcularDescuento(Integer cantidad) {
        if (cantidad >= UMBRAL_10) {
            return DIEZ_PORCIENTO;
        } else if (cantidad >= UMBRAL_5) {
            return CINCO_PORCIENTO;
        }
        return BigDecimal.ZERO;
    }

}
