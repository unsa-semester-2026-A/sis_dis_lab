package com.logifresh.transporte.service;

import com.logifresh.transporte.dto.TransporteEstadoRequest;
import com.logifresh.transporte.dto.TransporteRequest;
import com.logifresh.transporte.dto.TransporteResponse;
import com.logifresh.transporte.exception.TransporteNotFoundException;
import com.logifresh.transporte.model.EstadoTransporte;
import com.logifresh.transporte.model.Transporte;
import com.logifresh.transporte.repository.TransporteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransporteService {

    private final TransporteRepository repository;

    public TransporteService(TransporteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TransporteResponse asignar(TransporteRequest request) {
        Transporte transporte = new Transporte(
                request.getPedidoId(),
                request.getConductor(),
                request.getVehiculo()
        );
        return TransporteResponse.fromEntity(repository.save(transporte));
    }

    @Transactional(readOnly = true)
    public TransporteResponse obtenerPorId(Long id) {
        Transporte transporte = repository.findById(id)
                .orElseThrow(() -> new TransporteNotFoundException(id));
        return TransporteResponse.fromEntity(transporte);
    }

    @Transactional(readOnly = true)
    public List<TransporteResponse> listarTodas() {
        return repository.findAll().stream()
                .map(TransporteResponse::fromEntity)
                .toList();
    }

    @Transactional
    public TransporteResponse actualizarEstado(Long id, TransporteEstadoRequest request) {
        Transporte transporte = repository.findById(id)
                .orElseThrow(() -> new TransporteNotFoundException(id));

        EstadoTransporte nuevoEstado;
        try {
            nuevoEstado = EstadoTransporte.valueOf(request.getEstado());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Estado invalido: " + request.getEstado()
                    + ". Valores permitidos: ASIGNADO, EN_RUTA, ENTREGADO");
        }

        transporte.setEstado(nuevoEstado);
        return TransporteResponse.fromEntity(repository.save(transporte));
    }

}
