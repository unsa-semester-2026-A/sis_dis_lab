package com.logifresh.notificaciones.controller;

import com.logifresh.notificaciones.dto.NotificacionRequest;
import com.logifresh.notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NotificacionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificacionRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void crear_ShouldReturn201() {
        var request = new NotificacionRequest();
        request.setDestinatario("cliente@empresa.com");
        request.setAsunto("Pedido confirmado");
        request.setMensaje("Su pedido ha sido registrado exitosamente");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/notificaciones", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("destinatario")).isEqualTo("cliente@empresa.com");
        assertThat(response.getBody().get("asunto")).isEqualTo("Pedido confirmado");
        assertThat(response.getBody().get("mensaje")).isEqualTo("Su pedido ha sido registrado exitosamente");
        assertThat(response.getBody().get("estado")).isEqualTo("ENVIADA");
        assertThat(response.getBody().get("fechaEnvio")).isNotNull();
    }

    @Test
    void crear_WithInvalidData_ShouldReturn400() {
        var request = new NotificacionRequest();
        request.setDestinatario("");
        request.setAsunto("");
        request.setMensaje("");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/notificaciones", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void obtenerPorId_ShouldReturn200() {
        Long id = crearNotificacion("test@empresa.com", "Bienvenido", "Gracias por su compra");

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/notificaciones/{id}", Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(id.intValue());
        assertThat(response.getBody().get("destinatario")).isEqualTo("test@empresa.com");
        assertThat(response.getBody().get("asunto")).isEqualTo("Bienvenido");
        assertThat(response.getBody().get("mensaje")).isEqualTo("Gracias por su compra");
        assertThat(response.getBody().get("estado")).isEqualTo("ENVIADA");
    }

    @Test
    void obtenerPorId_WhenNotFound_ShouldReturn404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/notificaciones/{id}", Map.class, 999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listarTodas_ShouldReturn200() {
        crearNotificacion("cli1@empresa.com", "Oferta", "Productos en descuento");
        crearNotificacion("cli2@empresa.com", "Alerta", "Stock bajo");

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/api/notificaciones", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    private Long crearNotificacion(String destinatario, String asunto, String mensaje) {
        var request = new NotificacionRequest();
        request.setDestinatario(destinatario);
        request.setAsunto(asunto);
        request.setMensaje(mensaje);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/notificaciones", request, Map.class);

        return ((Number) response.getBody().get("id")).longValue();
    }

}
