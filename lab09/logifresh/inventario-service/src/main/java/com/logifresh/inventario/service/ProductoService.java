package com.logifresh.inventario.service;

import com.logifresh.inventario.dto.ProductoRequest;
import com.logifresh.inventario.dto.ProductoResponse;
import com.logifresh.inventario.dto.StockRequest;
import com.logifresh.inventario.exception.ProductoNotFoundException;
import com.logifresh.inventario.exception.StockInsuficienteException;
import com.logifresh.inventario.model.Producto;
import com.logifresh.inventario.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository repository;

    public ProductoService(ProductoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductoResponse registrar(ProductoRequest request) {
        Producto producto = new Producto(
                request.getCodigo(),
                request.getNombre(),
                request.getStock(),
                request.getStockMinimo()
        );
        producto.setFechaCreacion(LocalDateTime.now());
        return ProductoResponse.fromEntity(repository.save(producto));
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        return ProductoResponse.fromEntity(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return repository.findAll().stream()
                .map(ProductoResponse::fromEntity)
                .toList();
    }

    @Transactional
    public ProductoResponse actualizarStock(Long id, StockRequest request) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        producto.setStock(producto.getStock() + request.getCantidad());
        return ProductoResponse.fromEntity(repository.save(producto));
    }

    @Transactional
    public ProductoResponse descontarStock(Long id, StockRequest request) {
        Producto producto = repository.findById(id)
                .orElseThrow(() -> new ProductoNotFoundException(id));
        Integer cantidad = request.getCantidad();
        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException(id, producto.getStock(), cantidad);
        }
        producto.setStock(producto.getStock() - cantidad);
        return ProductoResponse.fromEntity(repository.save(producto));
    }

}
