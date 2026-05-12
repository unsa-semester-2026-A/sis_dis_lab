// Excepción personalizada para manejar errores relacionados con operaciones de tarjetas de crédito
public class CreditCardException extends Exception {

    // Constructor que recibe el mensaje de error
    public CreditCardException(String msg) {

        // Envía el mensaje a la clase Exception
        super(msg);
    }
}