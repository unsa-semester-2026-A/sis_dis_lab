package com.logifresh.pedidos.service;

import com.logifresh.pedidos.dto.NotificacionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

@Service
public class NotificacionesClient {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesClient.class);

    private final WebClient webClient;
    private final String baseUrl;
    private final int timeoutMs;

    public NotificacionesClient(@Qualifier("webClient") WebClient webClient,
                                @Value("${notificaciones.service.url}") String baseUrl,
                                @Value("${webclient.timeout.ms:5000}") int timeoutMs) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.timeoutMs = timeoutMs;
    }

    public NotificacionResponse crearNotificacion(String destinatario, String asunto, String mensaje) {
        log.info("Enviando notificacion a {} desde notificaciones-service", destinatario);
        try {
            Map<String, Object> body = Map.of(
                    "destinatario", destinatario,
                    "asunto", asunto,
                    "mensaje", mensaje
            );
            return webClient.post()
                    .uri(baseUrl)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(NotificacionResponse.class)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .block();
        } catch (Exception e) {
            log.warn("Error al enviar notificacion a {}: {}", destinatario, e.getMessage());
            throw e;
        }
    }

}
