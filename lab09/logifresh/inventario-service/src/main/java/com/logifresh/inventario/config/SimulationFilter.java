package com.logifresh.inventario.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimulationFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SimulationFilter.class);

    @Value("${simulate.failure:false}")
    private boolean simulateFailure;

    @Value("${simulate.delay:false}")
    private boolean simulateDelay;

    @Value("${simulate.delay.ms:5000}")
    private int delayMs;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        if (simulateDelay) {
            log.warn("SIMULATE_DELAY activo - retardando respuesta {}ms", delayMs);
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        if (simulateFailure) {
            log.warn("SIMULATE_FAILURE activo - retornando HTTP 500");
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Fallo simulado\",\"status\":500,\"service\":\"inventario-service\"}");
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
