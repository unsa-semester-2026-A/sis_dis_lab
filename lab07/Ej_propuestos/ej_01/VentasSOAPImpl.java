package Ej_propuestos.ej_01;

import java.util.ArrayList;
import java.util.List;
import javax.jws.WebService;

// Clase que implementa el servicio SOAP de ventas en linea.
@WebService(
    endpointInterface = "Ej_propuestos.ej_01.VentasSOAP",
    serviceName = "VentasSOAPService",
    targetNamespace = "http://ventas.soap/"
)
public class VentasSOAPImpl implements VentasSOAP {

    private List<Producto> inventario;

    public VentasSOAPImpl() {
        inventario = new ArrayList<Producto>();
        inventario.add(new Producto(101, "Laptop Gamer", 1200.0, 5));
        inventario.add(new Producto(102, "Teclado Mecanico", 85.5, 20));
        inventario.add(new Producto(103, "Mouse Optico", 35.0, 15));
    }

    @Override
    public Producto[] obtenerProductos() {
        return inventario.toArray(new Producto[0]);
    }

    @Override
    public Producto buscarProducto(int id) {
        for (Producto p : inventario) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    @Override
    public String realizarVenta(int idProducto, int cantidad) {
        for (Producto p : inventario) {
            if (p.getId() == idProducto) {
                if (p.getStock() >= cantidad) {
                    p.setStock(p.getStock() - cantidad);
                    double total = p.getPrecio() * cantidad;
                    return (
                        "Venta exitosa. ID Producto: " +
                        idProducto +
                        ". Cantidad: " +
                        cantidad +
                        ". Total cobrado: $" +
                        total +
                        ". Stock actual: " +
                        p.getStock()
                    );
                } else {
                    return (
                        "Error: Stock insuficiente. Unidades disponibles: " +
                        p.getStock()
                    );
                }
            }
        }
        return "Error: Producto con ID " + idProducto + " no existe.";
    }
}
