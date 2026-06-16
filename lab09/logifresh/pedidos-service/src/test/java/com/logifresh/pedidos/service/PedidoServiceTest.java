package com.logifresh.pedidos.service;

import com.logifresh.pedidos.dto.InventarioProductoResponse;
import com.logifresh.pedidos.dto.PedidoRequest;
import com.logifresh.pedidos.dto.PedidoResponse;
import com.logifresh.pedidos.exception.PedidoNotFoundException;
import com.logifresh.pedidos.exception.StockInsuficienteException;
import com.logifresh.pedidos.model.EstadoPedido;
import com.logifresh.pedidos.model.Pedido;
import com.logifresh.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository repository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private PedidoService service;

    private PedidoRequest requestBase;

    @BeforeEach
    void setUp() {
        requestBase = new PedidoRequest();
        requestBase.setCliente("Supermercado Central");
        requestBase.setProductoId(1L);
        requestBase.setPrecioUnitario(BigDecimal.valueOf(100));
    }

    @Test
    void calcularDescuento_CantidadMenor50_ShouldReturnZero() {
        assertEquals(BigDecimal.ZERO, service.calcularDescuento(1));
        assertEquals(BigDecimal.ZERO, service.calcularDescuento(49));
    }

    @Test
    void calcularDescuento_Cantidad50_ShouldReturn5() {
        assertEquals(BigDecimal.valueOf(5), service.calcularDescuento(50));
        assertEquals(BigDecimal.valueOf(5), service.calcularDescuento(99));
    }

    @Test
    void calcularDescuento_Cantidad100_ShouldReturn10() {
        assertEquals(BigDecimal.valueOf(10), service.calcularDescuento(100));
        assertEquals(BigDecimal.valueOf(10), service.calcularDescuento(200));
    }

    @Test
    void registrar_WithCantidad30_ShouldApplyNoDiscount() {
        requestBase.setCantidad(30);

        InventarioProductoResponse inventarioResponse = new InventarioProductoResponse();
        inventarioResponse.setStock(100);

        Pedido pedidoGuardado = crearPedido(1L, requestBase, BigDecimal.ZERO,
                BigDecimal.valueOf(3000), BigDecimal.valueOf(3000));

        when(inventarioClient.obtenerProducto(1L)).thenReturn(inventarioResponse);
        when(inventarioClient.descontarStock(1L, 30)).thenReturn(inventarioResponse);
        when(repository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        PedidoResponse response = service.registrar(requestBase);

        assertEquals(BigDecimal.ZERO.compareTo(response.getDescuento()), 0);
        assertEquals(BigDecimal.valueOf(3000).compareTo(response.getTotal()), 0);
    }

    @Test
    void registrar_WithCantidad50_ShouldApply5PercentDiscount() {
        requestBase.setCantidad(50);

        InventarioProductoResponse inventarioResponse = new InventarioProductoResponse();
        inventarioResponse.setStock(100);

        Pedido pedidoGuardado = crearPedido(2L, requestBase, BigDecimal.valueOf(5),
                BigDecimal.valueOf(5000), BigDecimal.valueOf(4750));

        when(inventarioClient.obtenerProducto(1L)).thenReturn(inventarioResponse);
        when(inventarioClient.descontarStock(1L, 50)).thenReturn(inventarioResponse);
        when(repository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        PedidoResponse response = service.registrar(requestBase);

        assertEquals(BigDecimal.valueOf(5).compareTo(response.getDescuento()), 0);
        assertEquals(BigDecimal.valueOf(4750).compareTo(response.getTotal()), 0);
    }

    @Test
    void registrar_WithCantidad100_ShouldApply10PercentDiscount() {
        requestBase.setCantidad(100);

        InventarioProductoResponse inventarioResponse = new InventarioProductoResponse();
        inventarioResponse.setStock(200);

        Pedido pedidoGuardado = crearPedido(3L, requestBase, BigDecimal.valueOf(10),
                BigDecimal.valueOf(10000), BigDecimal.valueOf(9000));

        when(inventarioClient.obtenerProducto(1L)).thenReturn(inventarioResponse);
        when(inventarioClient.descontarStock(1L, 100)).thenReturn(inventarioResponse);
        when(repository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        PedidoResponse response = service.registrar(requestBase);

        assertEquals(BigDecimal.valueOf(10).compareTo(response.getDescuento()), 0);
        assertEquals(BigDecimal.valueOf(9000).compareTo(response.getTotal()), 0);
    }

    @Test
    void registrar_WithSufficientStock_ShouldCheckAndDiscountInventory() {
        requestBase.setCantidad(10);

        InventarioProductoResponse inventarioResponse = new InventarioProductoResponse();
        inventarioResponse.setStock(50);

        Pedido pedidoGuardado = crearPedido(4L, requestBase, BigDecimal.ZERO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));

        when(inventarioClient.obtenerProducto(1L)).thenReturn(inventarioResponse);
        when(inventarioClient.descontarStock(1L, 10)).thenReturn(inventarioResponse);
        when(repository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        service.registrar(requestBase);

        verify(inventarioClient).obtenerProducto(1L);
        verify(inventarioClient).descontarStock(1L, 10);
    }

    @Test
    void registrar_WithInsufficientStock_ShouldThrowException() {
        requestBase.setCantidad(100);

        InventarioProductoResponse inventarioResponse = new InventarioProductoResponse();
        inventarioResponse.setStock(30);

        when(inventarioClient.obtenerProducto(1L)).thenReturn(inventarioResponse);

        assertThrows(StockInsuficienteException.class, () -> service.registrar(requestBase));
        verify(repository, never()).save(any());
        verify(inventarioClient, never()).descontarStock(anyLong(), anyInt());
    }

    @Test
    void obtenerPorId_WhenExists_ShouldReturnPedido() {
        Pedido pedido = crearPedido(1L, requestBase, BigDecimal.ZERO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        when(repository.findById(1L)).thenReturn(Optional.of(pedido));

        PedidoResponse response = service.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Supermercado Central", response.getCliente());
    }

    @Test
    void obtenerPorId_WhenNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void listarTodos_ShouldReturnAll() {
        Pedido p1 = crearPedido(1L, requestBase, BigDecimal.ZERO, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        Pedido p2 = crearPedido(2L, requestBase, BigDecimal.valueOf(5), BigDecimal.valueOf(2000), BigDecimal.valueOf(1900));

        when(repository.findAll()).thenReturn(List.of(p1, p2));

        List<PedidoResponse> responses = service.listarTodos();

        assertEquals(2, responses.size());
    }

    @Test
    void cancelar_ShouldChangeEstado() {
        Pedido pedido = crearPedido(1L, requestBase, BigDecimal.ZERO,
                BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        pedido.setEstado(EstadoPedido.CONFIRMADO);

        when(repository.findById(1L)).thenReturn(Optional.of(pedido));
        when(repository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoResponse response = service.cancelar(1L);

        assertEquals("CANCELADO", response.getEstado());
    }

    @Test
    void cancelar_WhenNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PedidoNotFoundException.class, () -> service.cancelar(99L));
    }

    private Pedido crearPedido(Long id, PedidoRequest req, BigDecimal descuento,
                               BigDecimal subtotal, BigDecimal total) {
        Pedido pedido = new Pedido();
        pedido.setId(id);
        pedido.setCliente(req.getCliente());
        pedido.setProductoId(req.getProductoId());
        pedido.setCantidad(req.getCantidad());
        pedido.setPrecioUnitario(req.getPrecioUnitario());
        pedido.setDescuento(descuento);
        pedido.setSubtotal(subtotal);
        pedido.setTotal(total);
        pedido.setEstado(EstadoPedido.CONFIRMADO);
        pedido.setFechaCreacion(LocalDateTime.now());
        return pedido;
    }

}
