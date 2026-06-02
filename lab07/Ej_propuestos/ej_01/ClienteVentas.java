package Ej_propuestos.ej_01;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

// Cliente Java que consume el servicio de ventas en linea remotamente.
public class ClienteVentas {
    public static void main(String[] args) throws Exception {
        // URL de definicion del servicio (WSDL)
        URL url = new URL("http://localhost:8082/ventas?wsdl");

        // QName identifica al servicio (namespace y local name)
        QName qname = new QName("http://ventas.soap/", "VentasSOAPService");

        // Creacion del servicio
        Service service = Service.create(url, qname);

        // Obtencion del puerto utilizando la interface
        VentasSOAP ventas = service.getPort(VentasSOAP.class);

        // 1. Mostrar catalogo de productos
        System.out.println("=== CATALOGO DE PRODUCTOS ===");
        Producto[] lista = ventas.obtenerProductos();
        for (Producto p : lista) {
            System.out.println("ID: " + p.getId() +
                               " | Nombre: " + p.getNombre() +
                               " | Precio: $" + p.getPrecio() +
                               " | Stock: " + p.getStock());
        }
        System.out.println("=============================");

        // 2. Intentar realizar una venta exitosa
        int idCompra = 102;
        int cantidadCompra = 3;
        System.out.println("\nComprando " + cantidadCompra + " unidades del producto ID: " + idCompra + "...");
        String resultado1 = ventas.realizarVenta(idCompra, cantidadCompra);
        System.out.println("Respuesta del servidor: " + resultado1);

        // 3. Intentar realizar una venta con stock insuficiente
        int idCompra2 = 101;
        int cantidadCompra2 = 10;
        System.out.println("\nComprando " + cantidadCompra2 + " unidades del producto ID: " + idCompra2 + "...");
        String resultado2 = ventas.realizarVenta(idCompra2, cantidadCompra2);
        System.out.println("Respuesta del servidor: " + resultado2);
    }
}
