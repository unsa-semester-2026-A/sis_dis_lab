package Ej_propuestos.ej_01;

import javax.xml.ws.Endpoint;

// Clase para publicar el servicio SOAP en una URL especifica.
public class Publicador {
    public static void main(String[] args) {
        // Se define el puerto 8081 para evitar conflictos con el puerto 8080
        String url = "http://localhost:8081/conversor";
        Endpoint.publish(url, new ConversorSOAPImpl());
        System.out.println("Servicio SOAP de Conversor de Temperatura activo en: " + url);
    }
}
