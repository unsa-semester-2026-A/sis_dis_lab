package com.logifresh.pedidos.service;

import com.logifresh.pedidos.dto.FacturaResponse;
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
public class FacturacionClient {

    private static final Logger log = LoggerFactory.getLogger(FacturacionClient.class);

    private final WebClient webClient;
    private final String baseUrl;
    private final int timeoutMs;

    public FacturacionClient(@Qualifier("webClient") WebClient webClient,
                             @Value("${facturacion.service.url}") String baseUrl,
                             @Value("${webclient.timeout.ms:5000}") int timeoutMs) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.timeoutMs = timeoutMs;
    }

    public FacturaResponse crearFactura(String numeroFactura, Long pedidoId,
                                         String cliente, Double monto) {
        log.info("Generando factura para pedido {} en facturacion-service", pedidoId);
        try {
            Map<String, Object> body = Map.of(
                    "numeroFactura", numeroFactura,
                    "pedidoId", pedidoId,
                    "cliente", cliente,
                    "monto", monto
            );
            return webClient.post()
                    .uri(baseUrl)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(FacturaResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(300))
                            .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .block();
        } catch (Exception e) {
            log.error("Error al generar factura para pedido {}: {}", pedidoId, e.getMessage());
            throw e;
        }
    }

}
