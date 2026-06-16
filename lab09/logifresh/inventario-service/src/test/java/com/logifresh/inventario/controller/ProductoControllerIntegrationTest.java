package com.logifresh.inventario.controller;

import com.logifresh.inventario.dto.ProductoRequest;
import com.logifresh.inventario.dto.StockRequest;
import com.logifresh.inventario.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductoControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductoRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void registrar_ShouldReturn201() {
        var request = new ProductoRequest();
        request.setCodigo("PRD-001");
        request.setNombre("Leche Entera");
        request.setStock(100);
        request.setStockMinimo(10);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/inventario/productos", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("codigo")).isEqualTo("PRD-001");
        assertThat(response.getBody().get("nombre")).isEqualTo("Leche Entera");
        assertThat(response.getBody().get("stock")).isEqualTo(100);
        assertThat(response.getBody().get("stockMinimo")).isEqualTo(10);
    }

    @Test
    void registrar_WithInvalidData_ShouldReturn400() {
        var request = new ProductoRequest();
        request.setCodigo("");
        request.setNombre("");
        request.setStock(-1);
        request.setStockMinimo(-1);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/inventario/productos", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void obtenerPorId_ShouldReturn200() {
        Long id = crearProducto("PRD-002", "Yogurt Natural", 50, 5);

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/inventario/productos/{id}", Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(id.intValue());
        assertThat(response.getBody().get("codigo")).isEqualTo("PRD-002");
        assertThat(response.getBody().get("nombre")).isEqualTo("Yogurt Natural");
        assertThat(response.getBody().get("stock")).isEqualTo(50);
    }

    @Test
    void obtenerPorId_WhenNotFound_ShouldReturn404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/inventario/productos/{id}", Map.class, 999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listarTodos_ShouldReturn200() {
        crearProducto("PRD-003", "Queso", 30, 5);
        crearProducto("PRD-004", "Mantequilla", 20, 3);

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/api/inventario/productos", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void actualizarStock_ShouldReturn200() {
        Long id = crearProducto("PRD-005", "Crema", 40, 5);

        var request = new StockRequest();
        request.setCantidad(10);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/inventario/productos/{id}/stock", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("stock")).isEqualTo(50);
    }

    @Test
    void descontarStock_ShouldReturn200() {
        Long id = crearProducto("PRD-006", "Helado", 60, 10);

        var request = new StockRequest();
        request.setCantidad(20);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/inventario/productos/{id}/descontar", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("stock")).isEqualTo(40);
    }

    @Test
    void descontarStock_WithInsufficientStock_ShouldReturn400() {
        Long id = crearProducto("PRD-007", "Pollo", 10, 2);

        var request = new StockRequest();
        request.setCantidad(200);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/inventario/productos/{id}/descontar", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void actualizarStock_WithNegativeCantidad_ShouldReturn400() {
        var request = new StockRequest();
        request.setCantidad(-5);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/inventario/productos/1/stock", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void descontarStock_WithCantidadCero_ShouldReturn400() {
        var request = new StockRequest();
        request.setCantidad(0);

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/inventario/productos/1/descontar", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private Long crearProducto(String codigo, String nombre, Integer stock, Integer stockMinimo) {
        var request = new ProductoRequest();
        request.setCodigo(codigo);
        request.setNombre(nombre);
        request.setStock(stock);
        request.setStockMinimo(stockMinimo);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/inventario/productos", request, Map.class);

        return ((Number) response.getBody().get("id")).longValue();
    }

}
