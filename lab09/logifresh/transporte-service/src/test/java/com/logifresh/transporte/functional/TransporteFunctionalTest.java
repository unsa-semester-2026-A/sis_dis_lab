package com.logifresh.transporte.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logifresh.transporte.repository.TransporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas funcionales de transporte")
class TransporteFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransporteRepository transporteRepository;

    @BeforeEach
    void cleanUp() {
        transporteRepository.deleteAll();
    }

    @Test
    @DisplayName("10. Asignacion de transporte retorna 201 Created con estado ASIGNADO")
    void asignarTransporte_ShouldReturn201() throws Exception {
        String requestJson = """
                {"pedidoId":1,"conductor":"Carlos Lopez","vehiculo":"CAMI-001 - Camara Fria"}
                """;

        mockMvc.perform(post("/api/transporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.pedidoId").value(1))
                .andExpect(jsonPath("$.conductor").value("Carlos Lopez"))
                .andExpect(jsonPath("$.vehiculo").value("CAMI-001 - Camara Fria"))
                .andExpect(jsonPath("$.estado").value("ASIGNADO"))
                .andExpect(jsonPath("$.fechaAsignacion").isNotEmpty());
    }

    @Test
    @DisplayName("10b. Asignacion con datos invalidos retorna 400 Bad Request")
    void asignarTransporte_ConDatosInvalidos_ShouldReturn400() throws Exception {
        String requestJson = """
                {"pedidoId":null,"conductor":"","vehiculo":""}
                """;

        mockMvc.perform(post("/api/transporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("10c. Actualizar estado de transporte cambia correctamente el estado")
    void actualizarEstado_ShouldUpdateEstado() throws Exception {
        String crearJson = """
                {"pedidoId":2,"conductor":"Maria Garcia","vehiculo":"CAM-002"}
                """;

        String response = mockMvc.perform(post("/api/transporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(crearJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        String estadoJson = """
                {"estado":"EN_RUTA"}
                """;

        mockMvc.perform(put("/api/transporte/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estadoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.estado").value("EN_RUTA"))
                .andExpect(jsonPath("$.pedidoId").value(2))
                .andExpect(jsonPath("$.conductor").value("Maria Garcia"));
    }

    @Test
    @DisplayName("10d. Actualizar estado con valor invalido retorna 400")
    void actualizarEstado_Invalido_ShouldReturn400() throws Exception {
        String response = mockMvc.perform(post("/api/transporte")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"pedidoId":3,"conductor":"Test","vehiculo":"V-001"}
                                """))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(put("/api/transporte/{id}/estado", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\":\"INEXISTENTE\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("10e. Listar transportes retorna todas las asignaciones")
    void listarTransportes_ShouldReturnAll() throws Exception {
        String t1 = """
                {"pedidoId":10,"conductor":"Conductor A","vehiculo":"V-001"}
                """;
        String t2 = """
                {"pedidoId":11,"conductor":"Conductor B","vehiculo":"V-002"}
                """;

        mockMvc.perform(post("/api/transporte")
                .contentType(MediaType.APPLICATION_JSON).content(t1));
        mockMvc.perform(post("/api/transporte")
                .contentType(MediaType.APPLICATION_JSON).content(t2));

        mockMvc.perform(get("/api/transporte"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("ASIGNADO"))
                .andExpect(jsonPath("$[1].estado").value("ASIGNADO"));
    }

}
