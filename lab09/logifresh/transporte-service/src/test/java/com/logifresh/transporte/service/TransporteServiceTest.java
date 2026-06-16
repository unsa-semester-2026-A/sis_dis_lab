package com.logifresh.transporte.service;

import com.logifresh.transporte.dto.TransporteEstadoRequest;
import com.logifresh.transporte.dto.TransporteRequest;
import com.logifresh.transporte.dto.TransporteResponse;
import com.logifresh.transporte.exception.TransporteNotFoundException;
import com.logifresh.transporte.model.EstadoTransporte;
import com.logifresh.transporte.model.Transporte;
import com.logifresh.transporte.repository.TransporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransporteServiceTest {

    @Mock
    private TransporteRepository repository;

    @InjectMocks
    private TransporteService service;

    private Transporte transporte;

    @BeforeEach
    void setUp() {
        transporte = new Transporte(1L, "Juan Perez", "CAMION-01");
        transporte.setId(1L);
        transporte.setFechaAsignacion(LocalDateTime.now());
    }

    @Test
    void asignar_ShouldReturnTransporteResponse() {
        TransporteRequest request = new TransporteRequest();
        request.setPedidoId(1L);
        request.setConductor("Juan Perez");
        request.setVehiculo("CAMION-01");

        when(repository.save(any(Transporte.class))).thenReturn(transporte);

        TransporteResponse response = service.asignar(request);

        assertNotNull(response);
        assertEquals(1L, response.getPedidoId());
        assertEquals("Juan Perez", response.getConductor());
        assertEquals("CAMION-01", response.getVehiculo());
        assertEquals(EstadoTransporte.ASIGNADO, response.getEstado());
        assertNotNull(response.getFechaAsignacion());

        ArgumentCaptor<Transporte> captor = ArgumentCaptor.forClass(Transporte.class);
        verify(repository).save(captor.capture());
        assertEquals(1L, captor.getValue().getPedidoId());
        assertEquals(EstadoTransporte.ASIGNADO, captor.getValue().getEstado());
    }

    @Test
    void obtenerPorId_WhenTransporteExists_ShouldReturnResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(transporte));

        TransporteResponse response = service.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1L, response.getPedidoId());
        assertEquals("Juan Perez", response.getConductor());
    }

    @Test
    void obtenerPorId_WhenTransporteNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransporteNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void listarTodas_ShouldReturnAllTransportes() {
        Transporte otro = new Transporte(2L, "Maria Lopez", "CAMION-02");
        otro.setId(2L);
        when(repository.findAll()).thenReturn(List.of(transporte, otro));

        List<TransporteResponse> responses = service.listarTodas();

        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getPedidoId());
        assertEquals(2L, responses.get(1).getPedidoId());
    }

    @Test
    void listarTodas_WhenEmpty_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<TransporteResponse> responses = service.listarTodas();

        assertTrue(responses.isEmpty());
    }

    @Test
    void actualizarEstado_ShouldUpdateEstado() {
        TransporteEstadoRequest request = new TransporteEstadoRequest();
        request.setEstado("EN_RUTA");

        when(repository.findById(1L)).thenReturn(Optional.of(transporte));
        when(repository.save(any(Transporte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransporteResponse response = service.actualizarEstado(1L, request);

        assertEquals(EstadoTransporte.EN_RUTA, response.getEstado());
    }

    @Test
    void actualizarEstado_ToEntregado_ShouldUpdateEstado() {
        TransporteEstadoRequest request = new TransporteEstadoRequest();
        request.setEstado("ENTREGADO");

        when(repository.findById(1L)).thenReturn(Optional.of(transporte));
        when(repository.save(any(Transporte.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransporteResponse response = service.actualizarEstado(1L, request);

        assertEquals(EstadoTransporte.ENTREGADO, response.getEstado());
    }

    @Test
    void actualizarEstado_WithInvalidEstado_ShouldThrowException() {
        TransporteEstadoRequest request = new TransporteEstadoRequest();
        request.setEstado("INVALIDO");

        when(repository.findById(1L)).thenReturn(Optional.of(transporte));

        assertThrows(IllegalArgumentException.class, () -> service.actualizarEstado(1L, request));
    }

    @Test
    void actualizarEstado_WhenTransporteNotFound_ShouldThrowException() {
        TransporteEstadoRequest request = new TransporteEstadoRequest();
        request.setEstado("EN_RUTA");

        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TransporteNotFoundException.class, () -> service.actualizarEstado(99L, request));
    }

}
