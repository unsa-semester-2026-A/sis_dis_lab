package com.logifresh.pedidos.controller;

import com.logifresh.pedidos.dto.PedidoRequest;
import com.logifresh.pedidos.dto.PedidoResponse;
import com.logifresh.pedidos.dto.ProcesarPedidoResponse;
import com.logifresh.pedidos.service.OrquestacionService;
import com.logifresh.pedidos.service.PedidoService;
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
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;
    private final OrquestacionService orquestacionService;

    public PedidoController(PedidoService service, OrquestacionService orquestacionService) {
        this.service = service;
        this.orquestacionService = orquestacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse registrar(@Valid @RequestBody PedidoRequest request) {
        return service.registrar(request);
    }

    @PostMapping("/procesar")
    @ResponseStatus(HttpStatus.CREATED)
    public ProcesarPedidoResponse procesar(@Valid @RequestBody PedidoRequest request) {
        return orquestacionService.procesar(request);
    }

    @GetMapping
    public List<PedidoResponse> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public PedidoResponse obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}/cancelar")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

}
