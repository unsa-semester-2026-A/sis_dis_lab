package com.logifresh.notificaciones.functional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logifresh.notificaciones.repository.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Pruebas funcionales de notificaciones")
class NotificacionesFunctionalTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificacionRepository notificacionRepository;

    @BeforeEach
    void cleanUp() {
        notificacionRepository.deleteAll();
    }

    @Test
    @DisplayName("9. Creacion de notificacion retorna 201 Created con estado ENVIADA")
    void crearNotificacion_ShouldReturn201() throws Exception {
        String requestJson = """
                {"destinatario":"cliente@logifresh.com","asunto":"Pedido confirmado","mensaje":"Su pedido ha sido procesado."}
                """;

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.destinatario").value("cliente@logifresh.com"))
                .andExpect(jsonPath("$.asunto").value("Pedido confirmado"))
                .andExpect(jsonPath("$.mensaje").value("Su pedido ha sido procesado."))
                .andExpect(jsonPath("$.estado").value("ENVIADA"))
                .andExpect(jsonPath("$.fechaEnvio").isNotEmpty());
    }

    @Test
    @DisplayName("9b. Notificacion con datos invalidos retorna 400 Bad Request")
    void crearNotificacion_ConDatosInvalidos_ShouldReturn400() throws Exception {
        String requestJson = """
                {"destinatario":"","asunto":"","mensaje":""}
                """;

        mockMvc.perform(post("/api/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("9c. Listar notificaciones retorna todas las creadas")
    void listarNotificaciones_ShouldReturnAll() throws Exception {
        String n1 = """
                {"destinatario":"a@test.com","asunto":"A","mensaje":"Mensaje A"}
                """;
        String n2 = """
                {"destinatario":"b@test.com","asunto":"B","mensaje":"Mensaje B"}
                """;

        mockMvc.perform(post("/api/notificaciones")
                .contentType(MediaType.APPLICATION_JSON).content(n1));
        mockMvc.perform(post("/api/notificaciones")
                .contentType(MediaType.APPLICATION_JSON).content(n2));

        mockMvc.perform(get("/api/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("ENVIADA"))
                .andExpect(jsonPath("$[1].estado").value("ENVIADA"));
    }

}
