package Ej_propuestos.ej_01;

import javax.jws.WebMethod;
import javax.jws.WebService;

// Interface que define el contrato del servicio de conversion de temperatura.
@WebService(targetNamespace = "http://conversor.soap/")
public interface ConversorSOAP {
    
    @WebMethod
    public double cToF(double c);
    
    @WebMethod
    public double fToC(double f);
}
