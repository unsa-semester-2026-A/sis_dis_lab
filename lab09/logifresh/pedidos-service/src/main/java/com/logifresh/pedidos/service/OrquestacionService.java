package com.logifresh.pedidos.service;

import com.logifresh.pedidos.dto.FacturaResponse;
import com.logifresh.pedidos.dto.InventarioProductoResponse;
import com.logifresh.pedidos.dto.NotificacionResponse;
import com.logifresh.pedidos.dto.PedidoRequest;
import com.logifresh.pedidos.dto.ProcesarPedidoResponse;
import com.logifresh.pedidos.dto.TransporteResponse;
import com.logifresh.pedidos.exception.OrquestacionException;
import com.logifresh.pedidos.exception.StockInsuficienteException;
import com.logifresh.pedidos.model.EstadoPedido;
import com.logifresh.pedidos.model.Pedido;
import com.logifresh.pedidos.repository.PedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class OrquestacionService {

    private static final Logger log = LoggerFactory.getLogger(OrquestacionService.class);
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final PedidoRepository repository;
    private final InventarioClient inventarioClient;
    private final FacturacionClient facturacionClient;
    private final TransporteClient transporteClient;
    private final NotificacionesClient notificacionesClient;

    public OrquestacionService(PedidoRepository repository,
                                InventarioClient inventarioClient,
                                FacturacionClient facturacionClient,
                                TransporteClient transporteClient,
                                NotificacionesClient notificacionesClient) {
        this.repository = repository;
        this.inventarioClient = inventarioClient;
        this.facturacionClient = facturacionClient;
        this.transporteClient = transporteClient;
        this.notificacionesClient = notificacionesClient;
    }

    @Transactional
    public ProcesarPedidoResponse procesar(PedidoRequest request) {
        Long productoId = request.getProductoId();
        Integer cantidad = request.getCantidad();

        BigDecimal descuento = calcularDescuento(cantidad);
        BigDecimal cant = BigDecimal.valueOf(cantidad);
        BigDecimal subtotal = request.getPrecioUnitario().multiply(cant);
        BigDecimal descuentoFactor = descuento.divide(CIEN, 4, RoundingMode.HALF_UP);
        BigDecimal descuentoAplicado = subtotal.multiply(descuentoFactor);
        BigDecimal total = subtotal.subtract(descuentoAplicado);

        log.info("=== INICIO PROCESAMIENTO PEDIDO ===");
        log.info("Cliente: {}, Producto: {}, Cantidad: {}", request.getCliente(), productoId, cantidad);

        // 1-2. Verificar y descontar stock en inventario-service
        log.info("[Paso 1/6] Consultando inventario para producto {}", productoId);
        InventarioProductoResponse producto = inventarioClient.obtenerProducto(productoId);
        if (producto.getStock() < cantidad) {
            log.error("Stock insuficiente para producto {}. Disponible: {}, solicitado: {}",
                    productoId, producto.getStock(), cantidad);
            throw new StockInsuficienteException(productoId, producto.getStock(), cantidad);
        }
        log.info("[Paso 2/6] Descontando {} unidades del producto {}", cantidad, productoId);
        inventarioClient.descontarStock(productoId, cantidad);

        // 3. Registrar pedido en base de datos local
        log.info("[Paso 3/6] Registrando pedido en base de datos");
        Pedido pedido = new Pedido();
        pedido.setCliente(request.getCliente());
        pedido.setProductoId(productoId);
        pedido.setCantidad(cantidad);
        pedido.setPrecioUnitario(request.getPrecioUnitario());
        pedido.setDescuento(descuento);
        pedido.setSubtotal(subtotal);
        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido = repository.save(pedido);
        log.info("Pedido {} registrado con estado CONFIRMADO", pedido.getId());

        // 4. Generar factura en facturacion-service
        FacturaResponse factura = null;
        try {
            log.info("[Paso 4/6] Generando factura para pedido {}", pedido.getId());
            String numeroFactura = "FAC-P" + pedido.getId() + "-" + UUID.randomUUID().toString().substring(0, 8);
            factura = facturacionClient.crearFactura(
                    numeroFactura,
                    pedido.getId(),
                    request.getCliente(),
                    total.doubleValue()
            );
            log.info("Factura {} generada exitosamente (ID={})", numeroFactura, factura.getId());
        } catch (Exception e) {
            log.error("[ROLLBACK] Error en facturacion para pedido {}: {}", pedido.getId(), e.getMessage());
            log.info("Cancelando pedido {} y restaurando stock de producto {}", pedido.getId(), productoId);
            pedido.setEstado(EstadoPedido.CANCELADO);
            repository.save(pedido);
            inventarioClient.actualizarStock(productoId, cantidad);
            log.info("Rollback completado para pedido {}", pedido.getId());
            throw new OrquestacionException(
                    "Error al generar factura para pedido " + pedido.getId(), e);
        }

        // 5. Asignar transporte
        TransporteResponse transporte = null;
        try {
            log.info("[Paso 5/6] Asignando transporte para pedido {}", pedido.getId());
            transporte = transporteClient.asignarTransporte(pedido.getId(), request.getCliente());
            log.info("Transporte asignado exitosamente (ID={})", transporte.getId());
        } catch (Exception e) {
            log.error("[INCIDENTE] Error al asignar transporte para pedido {}: {}",
                    pedido.getId(), e.getMessage());
            log.warn("El pedido {} permanece CONFIRMADO. Se requiere asignacion manual de transporte.",
                    pedido.getId());
        }

        // 6. Enviar notificacion
        NotificacionResponse notificacion = null;
        try {
            log.info("[Paso 6/6] Enviando notificacion para pedido {}", pedido.getId());
            notificacion = notificacionesClient.crearNotificacion(
                    request.getCliente(),
                    "Pedido confirmado #" + pedido.getId(),
                    "Su pedido #" + pedido.getId() + " ha sido procesado exitosamente. "
                            + "Monto total: $" + total
            );
            log.info("Notificacion enviada exitosamente (ID={})", notificacion.getId());
        } catch (Exception e) {
            log.warn("[ADVERTENCIA] Error al enviar notificacion para pedido {}: {}",
                    pedido.getId(), e.getMessage());
            log.warn("El pedido {} se proceso correctamente sin notificacion.", pedido.getId());
        }

        String estadoFinal = determinarEstado(factura != null, transporte != null, notificacion != null);

        ProcesarPedidoResponse response = new ProcesarPedidoResponse(
                pedido.getId(),
                factura != null ? factura.getId() : null,
                transporte != null ? transporte.getId() : null,
                notificacion != null ? notificacion.getId() : null,
                estadoFinal
        );

        log.info("=== PEDIDO {} PROCESADO (Estado: {}) ===", pedido.getId(), estadoFinal);
        return response;
    }

    private String determinarEstado(boolean facturaOk, boolean transporteOk, boolean notificacionOk) {
        if (facturaOk && transporteOk && notificacionOk) {
            return "PROCESADO";
        }
        if (facturaOk && transporteOk) {
            return "PROCESADO_SIN_NOTIFICACION";
        }
        if (facturaOk) {
            return "PROCESADO_SIN_TRANSPORTE";
        }
        return "ERROR";
    }

    private BigDecimal calcularDescuento(Integer cantidad) {
        if (cantidad >= 100) return BigDecimal.valueOf(10);
        if (cantidad >= 50) return BigDecimal.valueOf(5);
        return BigDecimal.ZERO;
    }

}
