package Ej_Resuelto;

import javax.jws.WebService;

// Clase que implementa el servicio SOAP de calculadora.
// Se asocia con la interface CalculadoraSOAP para definir el contrato.
@WebService(endpointInterface = "Ej_Resuelto.CalculadoraSOAP", serviceName = "CalculadoraSOAPService")
public class CalculadoraSOAPImpl implements CalculadoraSOAP {
    
    @Override
    public int sumar(int a, int b) {
        return a + b;
    }
}
