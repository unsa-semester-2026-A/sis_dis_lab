package Ej_Resuelto;

import javax.jws.WebMethod; 
import javax.jws.WebService;

// Interface que define el contrato del servicio SOAP de calculadora.
@WebService
public interface CalculadoraSOAP {
    @WebMethod
    /* Permite que el metodo aparezca en el contrato WSDL y 
    pueda ser invocado por clientes externos via SOAP.
    */ 
    public int sumar(int a, int b);
}