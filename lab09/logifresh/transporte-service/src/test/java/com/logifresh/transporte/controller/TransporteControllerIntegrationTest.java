package com.logifresh.transporte.controller;

import com.logifresh.transporte.dto.TransporteEstadoRequest;
import com.logifresh.transporte.dto.TransporteRequest;
import com.logifresh.transporte.repository.TransporteRepository;
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
class TransporteControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TransporteRepository repository;

    @BeforeEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void asignar_ShouldReturn201() {
        var request = new TransporteRequest();
        request.setPedidoId(1L);
        request.setConductor("Juan Perez");
        request.setVehiculo("CAMION-01");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/transporte", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("id")).isNotNull();
        assertThat(response.getBody().get("pedidoId")).isEqualTo(1);
        assertThat(response.getBody().get("conductor")).isEqualTo("Juan Perez");
        assertThat(response.getBody().get("vehiculo")).isEqualTo("CAMION-01");
        assertThat(response.getBody().get("estado")).isEqualTo("ASIGNADO");
        assertThat(response.getBody().get("fechaAsignacion")).isNotNull();
    }

    @Test
    void asignar_WithInvalidData_ShouldReturn400() {
        var request = new TransporteRequest();
        request.setPedidoId(null);
        request.setConductor("");
        request.setVehiculo("");

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/transporte", request, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void obtenerPorId_ShouldReturn200() {
        Long id = crearTransporte(1L, "Ana Ruiz", "CAMION-02");

        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/transporte/{id}", Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("id")).isEqualTo(id.intValue());
        assertThat(response.getBody().get("pedidoId")).isEqualTo(1);
        assertThat(response.getBody().get("conductor")).isEqualTo("Ana Ruiz");
        assertThat(response.getBody().get("vehiculo")).isEqualTo("CAMION-02");
        assertThat(response.getBody().get("estado")).isEqualTo("ASIGNADO");
    }

    @Test
    void obtenerPorId_WhenNotFound_ShouldReturn404() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/api/transporte/{id}", Map.class, 999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void listarTodas_ShouldReturn200() {
        crearTransporte(1L, "Carlos", "CAMION-03");
        crearTransporte(2L, "Lucia", "CAMION-04");

        ResponseEntity<List> response = restTemplate.getForEntity(
                "/api/transporte", List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void actualizarEstado_ShouldReturn200() {
        Long id = crearTransporte(1L, "Pedro", "CAMION-05");

        var request = new TransporteEstadoRequest();
        request.setEstado("EN_RUTA");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/transporte/{id}/estado", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("estado")).isEqualTo("EN_RUTA");
    }

    @Test
    void actualizarEstado_WithInvalidEstado_ShouldReturn400() {
        Long id = crearTransporte(1L, "Pedro", "CAMION-05");

        var request = new TransporteEstadoRequest();
        request.setEstado("INVALIDO");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/transporte/{id}/estado", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class, id);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void actualizarEstado_WhenNotFound_ShouldReturn404() {
        var request = new TransporteEstadoRequest();
        request.setEstado("EN_RUTA");

        ResponseEntity<Map> response = restTemplate.exchange(
                "/api/transporte/{id}/estado", HttpMethod.PUT,
                new org.springframework.http.HttpEntity<>(request), Map.class, 999L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Long crearTransporte(Long pedidoId, String conductor, String vehiculo) {
        var request = new TransporteRequest();
        request.setPedidoId(pedidoId);
        request.setConductor(conductor);
        request.setVehiculo(vehiculo);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/transporte", request, Map.class);

        return ((Number) response.getBody().get("id")).longValue();
    }

}
