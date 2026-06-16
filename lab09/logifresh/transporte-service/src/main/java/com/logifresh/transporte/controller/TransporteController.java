package com.logifresh.transporte.controller;

import com.logifresh.transporte.dto.TransporteEstadoRequest;
import com.logifresh.transporte.dto.TransporteRequest;
import com.logifresh.transporte.dto.TransporteResponse;
import com.logifresh.transporte.service.TransporteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transporte")
public class TransporteController {

    private final TransporteService service;

    public TransporteController(TransporteService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransporteResponse asignar(@Valid @RequestBody TransporteRequest request) {
        return service.asignar(request);
    }

    @GetMapping("/{id}")
    public TransporteResponse obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping
    public List<TransporteResponse> listarTodas() {
        return service.listarTodas();
    }

    @PutMapping("/{id}/estado")
    public TransporteResponse actualizarEstado(@PathVariable Long id,
                                                 @Valid @RequestBody TransporteEstadoRequest request) {
        return service.actualizarEstado(id, request);
    }

}
