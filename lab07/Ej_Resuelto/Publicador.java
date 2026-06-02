package Ej_Resuelto;
//Paso 2: Publicar el Servicio
import javax.xml.ws.Endpoint;
import Ej_Resuelto.CalculadoraSOAPImpl; //Esto se agrego
public class Publicador{
    public static void main(String[] args){
        Endpoint.publish( //levanta el servicio en una URL especifica 
        "http://localhost:8080/calculadora",
        new CalculadoraSOAPImpl()
        );
        System.out.println("Servicio SOAP activo");
    }
}
//Aqui el servicio ya esta corriendo y esperando clientes