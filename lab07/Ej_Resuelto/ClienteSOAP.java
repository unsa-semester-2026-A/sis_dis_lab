package Ej_Resuelto;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import Ej_Resuelto.CalculadoraSOAP;

// Paso 3: Consumidor Java
public class ClienteSOAP {
    public static void main(String[] args) throws Exception {
        // Direccion URL del WSDL del servicio web
        URL url = new URL("http://localhost:8080/calculadora?wsdl");
        
        // QName identifica el servicio dentro del WSDL (namespace + nombre local)
        QName qname = new QName("http://Ej_Resuelto/", "CalculadoraSOAPService");
        
        // Creamos la instancia del servicio
        Service service = Service.create(url, qname);
        
        // Obtenemos el puerto de acceso (proxy) del servicio
        CalculadoraSOAP calc = service.getPort(Ej_Resuelto.CalculadoraSOAP.class);
        
        // Llamada al metodo remoto
        System.out.println("Resultado de la suma remota (10 + 20): " + calc.sumar(10,20));
    }
}