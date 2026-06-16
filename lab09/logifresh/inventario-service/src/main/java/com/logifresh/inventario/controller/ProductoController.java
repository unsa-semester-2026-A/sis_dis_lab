package com.logifresh.inventario.controller;

import com.logifresh.inventario.dto.ProductoRequest;
import com.logifresh.inventario.dto.ProductoResponse;
import com.logifresh.inventario.dto.StockRequest;
import com.logifresh.inventario.service.ProductoService;
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
@RequestMapping("/api/inventario/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse registrar(@Valid @RequestBody ProductoRequest request) {
        return service.registrar(request);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @GetMapping
    public List<ProductoResponse> listarTodos() {
        return service.listarTodos();
    }

    @PutMapping("/{id}/stock")
    public ProductoResponse actualizarStock(@PathVariable Long id,
                                            @Valid @RequestBody StockRequest request) {
        return service.actualizarStock(id, request);
    }

    @PutMapping("/{id}/descontar")
    public ProductoResponse descontarStock(@PathVariable Long id,
                                           @Valid @RequestBody StockRequest request) {
        return service.descontarStock(id, request);
    }

}
