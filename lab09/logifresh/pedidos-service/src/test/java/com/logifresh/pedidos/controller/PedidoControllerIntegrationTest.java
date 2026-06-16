package com.logifresh.pedidos.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.logifresh.pedidos.dto.PedidoRequest;
import com.logifresh.pedidos.repository.PedidoRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PedidoControllerIntegrationTest {

    private static final WireMockServer wireMock;

    static {
        wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMock.start();
        String baseUrl = "http://localhost:" + wireMock.port();
        System.setProperty("inventario.service.url", baseUrl + "/api/inventario");
        System.setProperty("facturacion.service.url", baseUrl + "/api/facturas");
        System.setProperty("transporte.service.url", baseUrl + "/api/transporte");
        System.setProperty("notificaciones.service.url", baseUrl + "/api/notificaciones");
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMock != null) {
            wireMock.stop();
        }
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void setUp() {
        pedidoRepository.deleteAll();
        wireMock.resetAll();
        // Stubs por defecto para CRUD (registrar, listar, obtener, cancelar)
        wireMock.stubFor(get(urlPathMatching("/api/inventario/productos/\\d+"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"id":1,"codigo":"PRD-001","nombre":"Leche","stock":200,"stockMinimo":10}
                                """)));
        wireMock.stubFor(put(urlPathMatching("/api/inventario/productos/\\d+/descontar"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"id":1,"codigo":"PRD-001","nombre":"Leche","stock":190,"stockMinimo":10}
                                """)));
    }

    private void stubInventario(int stock) {
        wireMock.stubFor(get(urlPathMatching("/api/inventario/productos/\\d+"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"id":1,"codigo":"PRD-001","nombre":"Leche","stock":%d,"stockMinimo":10}
                                """.formatted(stock))));

        wireMock.stubFor(put(urlPathMatching("/api/inventario/productos/\\d+/descontar"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"id":1,"codigo":"PRD-001","nombre":"Leche","stock":%d,"stockMinimo":10}
                                """.formatted(stock))));
    }

    private void stubRestauracionStock() {
        wireMock.stubFor(put(urlPathMatching("/api/inventario/productos/\\d+/stock"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"id":1,"codigo":"PRD-001","nombre":"Leche","stock":100,"stockMinimo":10}
                                """)));
    }

    private void stubFacturacion(int status) {
        wireMock.stubFor(post(urlPathMatching("/api/facturas"))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(status == 201
                                ? """
                                {"id":10,"numeroFactura":"FAC-TEST","pedidoId":1,"cliente":"Test","monto":100.0}
                                """
                                : "{\"error\":\"Error en facturacion\"}")));
    }

    private void stubTransporte(int status) {
        wireMock.stubFor(post(urlPathMatching("/api/transporte"))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(status == 201
                                ? """
                                {"id":20,"pedidoId":1,"conductor":"Conductor-1","vehiculo":"Camion-1","estado":"ASIGNADO"}
                                """
                                : "{\"error\":\"Error en transporte\"}")));
    }

    private void stubNotificaciones(int status) {
        wireMock.stubFor(post(urlPathMatching("/api/notificaciones"))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(status == 201
                                ? """
                                {"id":30,"destinatario":"Test","asunto":"Pedido confirmado","estado":"ENVIADA"}
                                """
                                : "{\"error\":\"Error en notificacion\"}")));
    }

    @Test
    void registrar_ShouldReturn201() {
        var request = new PedidoRequest();
        request.setCliente("Supermercado Central");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("cliente")).isEqualTo("Supermercado Central");
        assertThat(response.getBody().get("productoId")).isEqualTo(1);
        assertThat(response.getBody().get("cantidad")).isEqualTo(10);
        assertThat(response.getBody().get("estado")).isEqualTo("CONFIRMADO");
        assertThat(response.getBody().get("descuento")).isEqualTo(0);
    }

    @Test
    void registrar_WithCantidad100_ShouldApply10Percent() {
        var request = new PedidoRequest();
        request.setCliente("Mayorista SA");
        request.setProductoId(1L);
        request.setCantidad(100);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("descuento")).isEqualTo(10);
        assertThat(((Number) response.getBody().get("subtotal")).intValue()).isEqualTo(10000);
        assertThat(((Number) response.getBody().get("total")).intValue()).isEqualTo(9000);
    }

    @Test
    void registrar_WithInvalidData_ShouldReturn400() {
        var request = new PedidoRequest();
        request.setCliente("");
        request.setCantidad(-1);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void obtenerPorId_ShouldReturn200() {
        Long id = crearPedido("Test Cliente", 1L, 5, BigDecimal.valueOf(200));

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/pedidos/{id}", Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(id.intValue());
        assertThat(response.getBody().get("cliente")).isEqualTo("Test Cliente");
        assertThat(response.getBody().get("cantidad")).isEqualTo(5);
    }

    @Test
    void obtenerPorId_WhenNotFound_ShouldReturn404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/pedidos/{id}", Map.class, 999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listarTodos_ShouldReturn200() {
        crearPedido("Cliente A", 1L, 10, BigDecimal.valueOf(50));
        crearPedido("Cliente B", 2L, 20, BigDecimal.valueOf(30));

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/api/pedidos", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void cancelar_ShouldReturn200() {
        Long id = crearPedido("Cancelable", 1L, 5, BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/pedidos/{id}/cancelar", HttpMethod.PUT, null, Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("estado")).isEqualTo("CANCELADO");
    }

    @Test
    void cancelar_WhenNotFound_ShouldReturn404() {
        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/pedidos/{id}/cancelar", HttpMethod.PUT, null, Map.class, 999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void procesar_FlujoCompleto_ShouldReturn201() {
        stubInventario(100);
        stubRestauracionStock();
        stubFacturacion(201);
        stubTransporte(201);
        stubNotificaciones(201);

        var request = new PedidoRequest();
        request.setCliente("Cliente Procesado");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos/procesar", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("pedidoId")).isNotNull();
        assertThat(response.getBody().get("facturaId")).isEqualTo(10);
        assertThat(response.getBody().get("transporteId")).isEqualTo(20);
        assertThat(response.getBody().get("notificacionId")).isEqualTo(30);
        assertThat(response.getBody().get("estado")).isEqualTo("PROCESADO");

        wireMock.verify(getRequestedFor(urlPathMatching("/api/inventario/productos/\\d+")));
        wireMock.verify(putRequestedFor(urlPathMatching("/api/inventario/productos/\\d+/descontar")));
        wireMock.verify(postRequestedFor(urlPathMatching("/api/facturas")));
        wireMock.verify(postRequestedFor(urlPathMatching("/api/transporte")));
        wireMock.verify(postRequestedFor(urlPathMatching("/api/notificaciones")));
    }

    @Test
    void procesar_StockInsuficiente_ShouldReturn400() {
        stubInventario(5);

        var request = new PedidoRequest();
        request.setCliente("Sin Stock SA");
        request.setProductoId(1L);
        request.setCantidad(50);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos/procesar", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("error").toString()).contains("Stock insuficiente");
    }

    @Test
    void procesar_FacturacionFalla_RealizaRollback() {
        stubInventario(100);
        stubRestauracionStock();
        stubFacturacion(500);
        stubTransporte(201);
        stubNotificaciones(201);

        var request = new PedidoRequest();
        request.setCliente("Rollback Test");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        restTemplate.postForEntity("/api/pedidos/procesar", request, Map.class);

        wireMock.verify(getRequestedFor(urlPathMatching("/api/inventario/productos/\\d+")));
        wireMock.verify(putRequestedFor(urlPathMatching("/api/inventario/productos/\\d+/descontar")));
        wireMock.verify(postRequestedFor(urlPathMatching("/api/facturas")));
        wireMock.verify(putRequestedFor(urlPathMatching("/api/inventario/productos/\\d+/stock")));
    }

    @Test
    void procesar_TransporteFalla_DegradacionGraceful() {
        stubInventario(100);
        stubRestauracionStock();
        stubFacturacion(201);
        stubTransporte(500);
        stubNotificaciones(201);

        var request = new PedidoRequest();
        request.setCliente("Degradado Test");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos/procesar", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("estado")).isEqualTo("PROCESADO_SIN_TRANSPORTE");
        assertThat(response.getBody().get("transporteId")).isNull();
    }

    @Test
    void procesar_NotificacionFalla_DegradacionGraceful() {
        stubInventario(100);
        stubRestauracionStock();
        stubFacturacion(201);
        stubTransporte(201);
        stubNotificaciones(500);

        var request = new PedidoRequest();
        request.setCliente("Degradado Notif");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos/procesar", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().get("estado")).isEqualTo("PROCESADO_SIN_NOTIFICACION");
        assertThat(response.getBody().get("notificacionId")).isNull();
    }

    @Test
    void procesar_TodosLosServiciosCaen_Error() {
        stubInventario(100);
        stubRestauracionStock();
        stubFacturacion(500);
        stubTransporte(500);
        stubNotificaciones(500);

        var request = new PedidoRequest();
        request.setCliente("Fallo Total");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos/procesar", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Long crearPedido(String cliente, Long productoId, Integer cantidad,
                             BigDecimal precioUnitario) {
        var request = new PedidoRequest();
        request.setCliente(cliente);
        request.setProductoId(productoId);
        request.setCantidad(cantidad);
        request.setPrecioUnitario(precioUnitario);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/pedidos", request, Map.class);

        return ((Number) response.getBody().get("id")).longValue();
    }

}
