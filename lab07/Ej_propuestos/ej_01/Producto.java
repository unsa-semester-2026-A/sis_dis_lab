package Ej_propuestos.ej_01;

import java.io.Serializable;

// Clase que representa un producto.
// Debe ser serializable e implementar un constructor vacio para ser transmitida en SOAP.
public class Producto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nombre;
    private double precio;
    private int stock;

    // Constructor vacio obligatorio para JAX-WS
    public Producto() {}

    public Producto(int id, String nombre, double precio, int stock) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
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

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
