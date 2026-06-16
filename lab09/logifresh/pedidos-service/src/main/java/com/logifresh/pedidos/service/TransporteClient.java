package com.logifresh.pedidos.service;

import com.logifresh.pedidos.dto.TransporteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class TransporteClient {

    private static final Logger log = LoggerFactory.getLogger(TransporteClient.class);

    private final WebClient webClient;
    private final String baseUrl;
    private final int timeoutMs;

    public TransporteClient(@Qualifier("webClient") WebClient webClient,
                            @Value("${transporte.service.url}") String baseUrl,
                            @Value("${webclient.timeout.ms:5000}") int timeoutMs) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.timeoutMs = timeoutMs;
    }

    public TransporteResponse asignarTransporte(Long pedidoId, String cliente) {
        log.info("Asignando transporte para pedido {} en transporte-service", pedidoId);
        try {
            Map<String, Object> body = Map.of(
                    "pedidoId", pedidoId,
                    "conductor", "Conductor-" + pedidoId,
                    "vehiculo", "Camion-" + pedidoId
            );
            return webClient.post()
                    .uri(baseUrl)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(TransporteResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(300))
                            .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .block();
        } catch (Exception e) {
            log.error("Error al asignar transporte para pedido {}: {}", pedidoId, e.getMessage());
            throw e;
        }
    }

}
