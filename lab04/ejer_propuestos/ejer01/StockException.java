// Excepción personalizada para manejar errores relacionados con el stock
public class StockException extends Exception {

    // Constructor que recibe el mensaje de error
    public StockException(String mensaje) {
        super(mensaje);
    }
}