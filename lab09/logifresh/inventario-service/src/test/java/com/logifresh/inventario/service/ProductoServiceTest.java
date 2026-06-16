package com.logifresh.inventario.service;

import com.logifresh.inventario.dto.ProductoRequest;
import com.logifresh.inventario.dto.ProductoResponse;
import com.logifresh.inventario.dto.StockRequest;
import com.logifresh.inventario.exception.ProductoNotFoundException;
import com.logifresh.inventario.exception.StockInsuficienteException;
import com.logifresh.inventario.model.Producto;
import com.logifresh.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("PRD-001", "Leche Entera", 100, 10);
        producto.setId(1L);
        producto.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void registrar_ShouldReturnProductoResponse() {
        ProductoRequest request = new ProductoRequest();
        request.setCodigo("PRD-001");
        request.setNombre("Leche Entera");
        request.setStock(100);
        request.setStockMinimo(10);

        when(repository.save(any(Producto.class))).thenReturn(producto);

        ProductoResponse response = service.registrar(request);

        assertNotNull(response);
        assertEquals("PRD-001", response.getCodigo());
        assertEquals("Leche Entera", response.getNombre());
        assertEquals(100, response.getStock());

        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(repository).save(captor.capture());
        assertEquals("PRD-001", captor.getValue().getCodigo());
    }

    @Test
    void obtenerPorId_WhenProductoExists_ShouldReturnResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        ProductoResponse response = service.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Leche Entera", response.getNombre());
    }

    @Test
    void obtenerPorId_WhenProductoNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void listarTodos_ShouldReturnAllProducts() {
        Producto otro = new Producto("PRD-002", "Yogurt Natural", 50, 5);
        otro.setId(2L);
        when(repository.findAll()).thenReturn(List.of(producto, otro));

        List<ProductoResponse> responses = service.listarTodos();

        assertEquals(2, responses.size());
        assertEquals("PRD-001", responses.get(0).getCodigo());
        assertEquals("PRD-002", responses.get(1).getCodigo());
    }

    @Test
    void listarTodos_WhenEmpty_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<ProductoResponse> responses = service.listarTodos();

        assertTrue(responses.isEmpty());
    }

    @Test
    void actualizarStock_ShouldAddCantidad() {
        StockRequest request = new StockRequest();
        request.setCantidad(10);

        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoResponse response = service.actualizarStock(1L, request);

        assertEquals(110, response.getStock());
    }

    @Test
    void descontarStock_WithSufficientStock_ShouldReduceStock() {
        StockRequest request = new StockRequest();
        request.setCantidad(30);

        when(repository.findById(1L)).thenReturn(Optional.of(producto));
        when(repository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductoResponse response = service.descontarStock(1L, request);

        assertEquals(70, response.getStock());
    }

    @Test
    void descontarStock_WithInsufficientStock_ShouldThrowException() {
        StockRequest request = new StockRequest();
        request.setCantidad(200);

        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        StockInsuficienteException ex = assertThrows(
                StockInsuficienteException.class,
                () -> service.descontarStock(1L, request)
        );

        assertTrue(ex.getMessage().contains("100"));
        assertTrue(ex.getMessage().contains("200"));
        assertEquals(100, producto.getStock());
    }

    @Test
    void descontarStock_WhenProductNotFound_ShouldThrowException() {
        StockRequest request = new StockRequest();
        request.setCantidad(5);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> service.descontarStock(99L, request));
    }

    @Test
    void actualizarStock_WhenProductNotFound_ShouldThrowException() {
        StockRequest request = new StockRequest();
        request.setCantidad(10);

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductoNotFoundException.class, () -> service.actualizarStock(99L, request));
    }

}
