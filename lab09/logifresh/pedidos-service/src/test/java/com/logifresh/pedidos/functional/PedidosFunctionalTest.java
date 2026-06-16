package com.logifresh.pedidos.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logifresh.pedidos.dto.FacturaResponse;
import com.logifresh.pedidos.dto.InventarioProductoResponse;
import com.logifresh.pedidos.dto.NotificacionResponse;
import com.logifresh.pedidos.dto.PedidoRequest;
import com.logifresh.pedidos.dto.TransporteResponse;
import com.logifresh.pedidos.repository.PedidoRepository;
import com.logifresh.pedidos.service.FacturacionClient;
import com.logifresh.pedidos.service.InventarioClient;
import com.logifresh.pedidos.service.NotificacionesClient;
import com.logifresh.pedidos.service.TransporteClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas funcionales de pedidos")
class PedidosFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PedidoRepository pedidoRepository;

    @BeforeEach
    void cleanUp() {
        pedidoRepository.deleteAll();
        TestConfig.stockDisponible = 200;
    }

    @TestConfiguration
    static class TestConfig {

        static int stockDisponible = 200;

        @Bean
        @Primary
        public InventarioClient inventarioClient() {
            return new InventarioClient(null, "http://fake", 5000) {
                @Override
                public InventarioProductoResponse obtenerProducto(Long productoId) {
                    InventarioProductoResponse r = new InventarioProductoResponse();
                    r.setId(productoId);
                    r.setCodigo("PRD-" + productoId);
                    r.setNombre("Producto " + productoId);
                    r.setStock(stockDisponible);
                    r.setStockMinimo(10);
                    return r;
                }

                @Override
                public InventarioProductoResponse descontarStock(Long productoId, Integer cantidad) {
                    InventarioProductoResponse r = new InventarioProductoResponse();
                    r.setId(productoId);
                    r.setStock(stockDisponible - cantidad);
                    return r;
                }

                @Override
                public InventarioProductoResponse actualizarStock(Long productoId, Integer cantidad) {
                    InventarioProductoResponse r = new InventarioProductoResponse();
                    r.setId(productoId);
                    r.setStock(stockDisponible + cantidad);
                    return r;
                }
            };
        }

        @Bean
        @Primary
        public FacturacionClient facturacionClient() {
            return new FacturacionClient(null, "http://fake", 5000) {
                @Override
                public FacturaResponse crearFactura(String numeroFactura, Long pedidoId,
                                                     String cliente, Double monto) {
                    FacturaResponse r = new FacturaResponse();
                    r.setId(pedidoId);
                    r.setNumeroFactura(numeroFactura);
                    r.setPedidoId(pedidoId);
                    r.setCliente(cliente);
                    r.setMonto(monto);
                    return r;
                }
            };
        }

        @Bean
        @Primary
        public TransporteClient transporteClient() {
            return new TransporteClient(null, "http://fake", 5000) {
                @Override
                public TransporteResponse asignarTransporte(Long pedidoId, String cliente) {
                    TransporteResponse r = new TransporteResponse();
                    r.setId(pedidoId);
                    r.setPedidoId(pedidoId);
                    r.setConductor("Conductor-" + pedidoId);
                    r.setVehiculo("Camion-" + pedidoId);
                    r.setEstado("ASIGNADO");
                    return r;
                }
            };
        }

        @Bean
        @Primary
        public NotificacionesClient notificacionesClient() {
            return new NotificacionesClient(null, "http://fake", 5000) {
                @Override
                public NotificacionResponse crearNotificacion(String destinatario, String asunto,
                                                               String mensaje) {
                    NotificacionResponse r = new NotificacionResponse();
                    r.setId(1L);
                    r.setDestinatario(destinatario);
                    r.setAsunto(asunto);
                    r.setEstado("ENVIADA");
                    return r;
                }
            };
        }
    }

    @Test
    @DisplayName("1. Registro correcto de pedido retorna 201 Created")
    void registroCorrecto() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setCliente("Cliente Prueba");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.cliente").value("Cliente Prueba"))
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    @DisplayName("2. Pedido con inventario suficiente se registra exitosamente (stock 200 >= cantidad 50)")
    void pedidoConInventarioSuficiente() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setCliente("Stock Suficiente SA");
        request.setProductoId(1L);
        request.setCantidad(50);
        request.setPrecioUnitario(BigDecimal.valueOf(80));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.cantidad").value(50))
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    @DisplayName("3. Pedido con inventario insuficiente retorna 400 Bad Request")
    void pedidoConInventarioInsuficiente() throws Exception {
        TestConfig.stockDisponible = 5;

        PedidoRequest request = new PedidoRequest();
        request.setCliente("Sin Stock SA");
        request.setProductoId(1L);
        request.setCantidad(50);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(
                        org.hamcrest.Matchers.containsString("Stock insuficiente")));
    }

    @Test
    @DisplayName("4. Cancelacion de pedido cambia estado a CANCELADO")
    void cancelacionDePedido() throws Exception {
        Long id = crearPedido("Cancelable", 1L, 5, BigDecimal.valueOf(100));

        mockMvc.perform(put("/api/pedidos/{id}/cancelar", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    @Test
    @DisplayName("5. Pedido con cantidad 50 aplica descuento del 5%")
    void descuento5Porciento() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setCliente("Descuento5");
        request.setProductoId(1L);
        request.setCantidad(50);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descuento").value(5))
                .andExpect(jsonPath("$.subtotal").value(5000))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.cantidad").value(50));
    }

    @Test
    @DisplayName("6. Pedido con cantidad 100 aplica descuento del 10%")
    void descuento10Porciento() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setCliente("Descuento10 SA");
        request.setProductoId(1L);
        request.setCantidad(100);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descuento").value(10))
                .andExpect(jsonPath("$.subtotal").value(10000))
                .andExpect(jsonPath("$.total").isNumber())
                .andExpect(jsonPath("$.cantidad").value(100));
    }

    @Test
    @DisplayName("7. Procesar pedido completo genera factura automaticamente")
    void procesarPedidoGeneraFactura() throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setCliente("Procesado Test");
        request.setProductoId(1L);
        request.setCantidad(10);
        request.setPrecioUnitario(BigDecimal.valueOf(100));

        mockMvc.perform(post("/api/pedidos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pedidoId").isNumber())
                .andExpect(jsonPath("$.facturaId").isNumber())
                .andExpect(jsonPath("$.transporteId").isNumber())
                .andExpect(jsonPath("$.notificacionId").isNumber())
                .andExpect(jsonPath("$.estado").value("PROCESADO"));
    }

    private Long crearPedido(String cliente, Long productoId, Integer cantidad,
                             BigDecimal precioUnitario) throws Exception {
        PedidoRequest request = new PedidoRequest();
        request.setCliente(cliente);
        request.setProductoId(productoId);
        request.setCantidad(cantidad);
        request.setPrecioUnitario(precioUnitario);

        String response = mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

}
