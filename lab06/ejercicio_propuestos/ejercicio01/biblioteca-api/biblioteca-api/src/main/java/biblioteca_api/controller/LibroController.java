package biblioteca_api.controller;

import biblioteca_api.model.Libro;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/libros")
public class LibroController {

    private List<Libro> libros = new ArrayList<>(Arrays.asList(
            new Libro(1, "Clean Code", "Robert Martin", 2008),
            new Libro(2, "The Pragmatic Programmer", "Hunt & Thomas", 1999)
    ));

    private int nextId = 3;

    // GET /libros
    @GetMapping
    public List<Libro> listar() {
        return libros;
    }

    // POST /libros
    @PostMapping
    public Libro agregar(@RequestBody Libro libro) {
        System.out.println(">>> Petición POST recibida en /libros");
        System.out.println(">>> Datos recibidos: Nombre=" + libro.getNombre() + ", Autor=" + libro.getAutor() + ", Año=" + libro.getAnio());
        
        libro.setId(nextId++);
        libros.add(libro);
        //System.out.println("Libro registrado: " + libro.getNombre()); // Debug en consola
        return libro;
    }

    // GET /libros/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Libro> buscar(@PathVariable int id) {
        return libros.stream()
                .filter(l -> l.getId() == id)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /libros/{id}
    @DeleteMapping("/{id}")
    public Map<String, String> eliminar(@PathVariable int id) {

        libros.removeIf(l -> l.getId() == id);

        return Map.of("mensaje", "Libro eliminado");
    }

    // DEPURADOR: Captura errores de JSON y los imprime en la consola del servidor
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleJsonError(HttpMessageNotReadableException e) {
        System.err.println("ERROR DE DESERIALIZACIÓN ---");
        System.err.println("Causa: " + e.getMessage());
        System.err.println("--------------------------------");
    }
}