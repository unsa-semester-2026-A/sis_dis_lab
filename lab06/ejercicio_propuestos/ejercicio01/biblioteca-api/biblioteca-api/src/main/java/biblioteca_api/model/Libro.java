package biblioteca_api.model;

public class Libro {

    private int id;
    private String nombre;
    private String autor;
    private int anio;

    public Libro() {
    }

    public Libro(String nombre, String autor, int anio) {
        this.nombre = nombre;
        this.autor = autor;
        this.anio = anio;
    }

    public Libro(int id, String nombre, String autor, int anio) {
        this.id = id;
        this.nombre = nombre;
        this.autor = autor;
        this.anio = anio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }
}