package Ej_propuestos.ej_01;

import javax.xml.ws.Endpoint;

// Clase para publicar el servicio SOAP de ventas en linea.
public class PublicadorVentas {
    public static void main(String[] args) {
        // Se define el puerto 8082 para el servicio de ventas
        String url = "http://localhost:8082/ventas";
        Endpoint.publish(url, new VentasSOAPImpl());
        System.out.println("Servicio SOAP de Ventas activo en: " + url);
    }
}
