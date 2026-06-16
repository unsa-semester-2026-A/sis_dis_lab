package com.logifresh.inventario.concurrency;

import com.logifresh.inventario.dto.StockRequest;
import com.logifresh.inventario.exception.StockInsuficienteException;
import com.logifresh.inventario.model.Producto;
import com.logifresh.inventario.repository.ProductoRepository;
import com.logifresh.inventario.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Prueba de concurrencia - inventario-service")
class InventarioConcurrenciaTest {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

    private static final int STOCK_INICIAL = 100;
    private static final int CANTIDAD_POR_PEDIDO = 3;
    private static final int NUMERO_SOLICITUDES = 50;

    private Long productoId;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
        Producto producto = new Producto("CONCURSO", "Producto Prueba Concurrencia",
                STOCK_INICIAL, 10);
        producto = productoRepository.save(producto);
        productoId = producto.getId();
    }

    @Test
    @DisplayName("50 solicitudes concurrentes descontando stock del mismo producto con @Version")
    void descontarStockConcurrente() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(NUMERO_SOLICITUDES);
        CountDownLatch disparador = new CountDownLatch(1);
        List<Future<Resultado>> futuros = new ArrayList<>();
        AtomicInteger contadorExitos = new AtomicInteger();

        Instant inicio = Instant.now();

        for (int i = 0; i < NUMERO_SOLICITUDES; i++) {
            futuros.add(executor.submit(() -> {
                disparador.await();
                StockRequest request = new StockRequest();
                request.setCantidad(CANTIDAD_POR_PEDIDO);
                try {
                    productoService.descontarStock(productoId, request);
                    contadorExitos.incrementAndGet();
                    return new Resultado(TipoResultado.EXITO, null);
                } catch (StockInsuficienteException e) {
                    return new Resultado(TipoResultado.STOCK_INSUFICIENTE, e.getMessage());
                } catch (ObjectOptimisticLockingFailureException e) {
                    return new Resultado(TipoResultado.OPTIMISTIC_LOCK, e.getMessage());
                } catch (Exception e) {
                    return new Resultado(TipoResultado.OTRO_ERROR,
                            e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }));
        }

        disparador.countDown();
        executor.shutdown();
        executor.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS);

        Instant fin = Instant.now();
        long duracionMs = Duration.between(inicio, fin).toMillis();

        int exitos = 0;
        int stockInsuficiente = 0;
        int optimisticLocks = 0;
        int otrosErrores = 0;

        for (Future<Resultado> f : futuros) {
            Resultado r = f.get();
            switch (r.tipo) {
                case EXITO -> exitos++;
                case STOCK_INSUFICIENTE -> stockInsuficiente++;
                case OPTIMISTIC_LOCK -> optimisticLocks++;
                case OTRO_ERROR -> otrosErrores++;
            }
        }

        final int fExitos = exitos;
        final int fStockInsuficiente = stockInsuficiente;
        final int fOptimisticLocks = optimisticLocks;
        final int fOtrosErrores = otrosErrores;

        Producto productoFinal = productoRepository.findById(productoId).orElseThrow();
        int stockFinal = productoFinal.getStock();
        Long versionFinal = productoFinal.getVersion();
        int stockEsperado = STOCK_INICIAL - (exitos * CANTIDAD_POR_PEDIDO);

        System.out.println("==========================================");
        System.out.println("  PRUEBA DE CONCURRENCIA - INVENTARIO");
        System.out.println("==========================================");
        System.out.println("  Stock inicial:         " + STOCK_INICIAL);
        System.out.println("  Cantidad por solicitud: " + CANTIDAD_POR_PEDIDO);
        System.out.println("  Solicitudes totales:    " + NUMERO_SOLICITUDES);
        System.out.println("  Duracion total:         " + duracionMs + " ms");
        System.out.println("------------------------------------------");
        System.out.println("  Exitosas:               " + fExitos);
        System.out.println("  Stock insuficiente:     " + fStockInsuficiente);
        System.out.println("  Optimistic locks:       " + fOptimisticLocks);
        System.out.println("  Otros errores:          " + fOtrosErrores);
        System.out.println("------------------------------------------");
        System.out.println("  Stock final:            " + stockFinal);
        System.out.println("  Stock final esperado:   " + stockEsperado);
        System.out.println("  Version final:          " + versionFinal);
        System.out.println("  Version incrementos:    " + (versionFinal != null ? versionFinal - 1 : 0));
        System.out.println("==========================================");

        assertAll("Validaciones de concurrencia",
            () -> assertTrue(stockFinal >= 0,
                    "El stock final (" + stockFinal + ") no debe ser negativo"),

            () -> assertEquals(stockEsperado, stockFinal,
                    "Stock final debe ser stock inicial menos descuentos exitosos"),

            () -> assertTrue(fExitos > 0,
                    "Al menos un descuento debe haber sido exitoso"),

            () -> assertEquals(fExitos + fStockInsuficiente + fOptimisticLocks + fOtrosErrores,
                    NUMERO_SOLICITUDES,
                    "La suma de resultados debe igualar el total de solicitudes"),

            () -> assertEquals(0, fOtrosErrores,
                    "No deben ocurrir errores inesperados. Ocurrieron: " + fOtrosErrores),

            () -> assertTrue(fStockInsuficiente + fOptimisticLocks > 0,
                    "Deben haberse producido al menos un error de concurrencia o stock insuficiente "
                    + "(stock=" + STOCK_INICIAL + ", cada descuento=" + CANTIDAD_POR_PEDIDO
                    + ", max exitos posibles=" + (STOCK_INICIAL / CANTIDAD_POR_PEDIDO) + ")"),

            () -> assertTrue(duracionMs < 30000,
                    "La prueba debe completarse en menos de 30 segundos")
        );
    }

    private enum TipoResultado {
        EXITO,
        STOCK_INSUFICIENTE,
        OPTIMISTIC_LOCK,
        OTRO_ERROR
    }

    private record Resultado(TipoResultado tipo, String mensaje) {
    }

}
