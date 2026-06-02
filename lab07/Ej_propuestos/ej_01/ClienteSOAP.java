package Ej_propuestos.ej_01;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

// Cliente Java que consume el servicio de conversion de temperatura de forma remota.
public class ClienteSOAP {
    public static void main(String[] args) throws Exception {
        // URL de definicion del servicio (WSDL)
        URL url = new URL("http://localhost:8081/conversor?wsdl");
        
        // QName identifica al servicio (namespace y local name)
        QName qname = new QName("http://conversor.soap/", "ConversorSOAPService");
        
        // Creacion del servicio
        Service service = Service.create(url, qname);
        
        // Obtencion del puerto utilizando la interface
        ConversorSOAP conversor = service.getPort(ConversorSOAP.class);
        
        // Pruebas de conversion
        double c = 30.0;
        double f = conversor.cToF(c);
        System.out.println("Llamando a cToF(" + c + ")... Resultado: " + f + " Fahrenheit");
        
        double f2 = 86.0;
        double c2 = conversor.fToC(f2);
        System.out.println("Llamando a fToC(" + f2 + ")... Resultado: " + c2 + " Celsius");
    }
}
