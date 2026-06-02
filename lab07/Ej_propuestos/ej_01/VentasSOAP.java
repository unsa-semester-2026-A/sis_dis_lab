package Ej_propuestos.ej_01;

import javax.jws.WebMethod;
import javax.jws.WebService;

// Interface del servicio SOAP para ventas en linea.
@WebService(targetNamespace = "http://ventas.soap/")
public interface VentasSOAP {
    @WebMethod
    public Producto[] obtenerProductos();

    @WebMethod
    public Producto buscarProducto(int id);

    @WebMethod
    public String realizarVenta(int idProducto, int cantidad);
}
