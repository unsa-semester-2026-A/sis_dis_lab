package com.logifresh.pedidos.service;

import com.logifresh.pedidos.dto.InventarioProductoResponse;
import com.logifresh.pedidos.exception.InventarioServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Service
public class InventarioClient {

    private static final Logger log = LoggerFactory.getLogger(InventarioClient.class);

    private final WebClient webClient;
    private final String baseUrl;
    private final int timeoutMs;

    public InventarioClient(@Qualifier("webClient") WebClient webClient,
                            @Value("${inventario.service.url}") String baseUrl,
                            @Value("${webclient.timeout.ms:5000}") int timeoutMs) {
        this.webClient = webClient;
        this.baseUrl = baseUrl;
        this.timeoutMs = timeoutMs;
    }

    public InventarioProductoResponse obtenerProducto(Long productoId) {
        try {
            return webClient.get()
                    .uri(baseUrl + "/productos/{id}", productoId)
                    .retrieve()
                    .bodyToMono(InventarioProductoResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(300))
                            .filter(throwable -> !(throwable instanceof InventarioServiceException))
                            .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .block();
        } catch (Exception e) {
            log.error("Error al consultar producto {} en inventario-service: {}", productoId, e.getMessage());
            throw new InventarioServiceException(
                    "Error al consultar producto " + productoId + ": " + e.getMessage());
        }
    }

    public InventarioProductoResponse descontarStock(Long productoId, Integer cantidad) {
        try {
            return webClient.put()
                    .uri(baseUrl + "/productos/{id}/descontar", productoId)
                    .bodyValue(Map.of("cantidad", cantidad))
                    .retrieve()
                    .bodyToMono(InventarioProductoResponse.class)
                    .retryWhen(Retry.backoff(2, Duration.ofMillis(300))
                            .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .block();
        } catch (Exception e) {
            log.error("Error al descontar stock del producto {}: {}", productoId, e.getMessage());
            throw new InventarioServiceException(
                    "Error al descontar stock del producto " + productoId + ": " + e.getMessage());
        }
    }

    public InventarioProductoResponse actualizarStock(Long productoId, Integer cantidad) {
        try {
            return webClient.put()
                    .uri(baseUrl + "/productos/{id}/stock", productoId)
                    .bodyValue(Map.of("cantidad", cantidad))
                    .retrieve()
                    .bodyToMono(InventarioProductoResponse.class)
                    .timeout(Duration.ofMillis(timeoutMs))
                    .block();
        } catch (Exception e) {
            log.error("Error al restaurar stock del producto {}: {}", productoId, e.getMessage());
            throw new InventarioServiceException(
                    "Error al restaurar stock del producto " + productoId + ": " + e.getMessage());
        }
    }

}
