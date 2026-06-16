package com.logifresh.notificaciones.service;

import com.logifresh.notificaciones.dto.NotificacionRequest;
import com.logifresh.notificaciones.dto.NotificacionResponse;
import com.logifresh.notificaciones.exception.NotificacionNotFoundException;
import com.logifresh.notificaciones.model.EstadoNotificacion;
import com.logifresh.notificaciones.model.Notificacion;
import com.logifresh.notificaciones.repository.NotificacionRepository;
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
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repository;

    @InjectMocks
    private NotificacionService service;

    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        notificacion = new Notificacion("cliente@empresa.com", "Pedido confirmado", "Su pedido ha sido registrado");
        notificacion.setId(1L);
        notificacion.setFechaEnvio(LocalDateTime.now());
    }

    @Test
    void crear_ShouldReturnNotificacionResponse() {
        NotificacionRequest request = new NotificacionRequest();
        request.setDestinatario("cliente@empresa.com");
        request.setAsunto("Pedido confirmado");
        request.setMensaje("Su pedido ha sido registrado");

        when(repository.save(any(Notificacion.class))).thenReturn(notificacion);

        NotificacionResponse response = service.crear(request);

        assertNotNull(response);
        assertEquals("cliente@empresa.com", response.getDestinatario());
        assertEquals("Pedido confirmado", response.getAsunto());
        assertEquals("Su pedido ha sido registrado", response.getMensaje());
        assertEquals(EstadoNotificacion.ENVIADA, response.getEstado());
        assertNotNull(response.getFechaEnvio());

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repository).save(captor.capture());
        assertEquals("cliente@empresa.com", captor.getValue().getDestinatario());
        assertEquals(EstadoNotificacion.ENVIADA, captor.getValue().getEstado());
        assertNotNull(captor.getValue().getFechaEnvio());
    }

    @Test
    void obtenerPorId_WhenNotificacionExists_ShouldReturnResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(notificacion));

        NotificacionResponse response = service.obtenerPorId(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("cliente@empresa.com", response.getDestinatario());
        assertEquals("Pedido confirmado", response.getAsunto());
    }

    @Test
    void obtenerPorId_WhenNotificacionNotFound_ShouldThrowException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotificacionNotFoundException.class, () -> service.obtenerPorId(99L));
    }

    @Test
    void listarTodas_ShouldReturnAllNotificaciones() {
        Notificacion otra = new Notificacion("otro@empresa.com", "Envio programado", "Su envio esta en camino");
        otra.setId(2L);
        when(repository.findAll()).thenReturn(List.of(notificacion, otra));

        List<NotificacionResponse> responses = service.listarTodas();

        assertEquals(2, responses.size());
        assertEquals("cliente@empresa.com", responses.get(0).getDestinatario());
        assertEquals("otro@empresa.com", responses.get(1).getDestinatario());
    }

    @Test
    void listarTodas_WhenEmpty_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        List<NotificacionResponse> responses = service.listarTodas();

        assertTrue(responses.isEmpty());
    }

}
