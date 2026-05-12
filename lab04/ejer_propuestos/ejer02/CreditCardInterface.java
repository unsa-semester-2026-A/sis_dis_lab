import java.rmi.Remote;

// Interfaz remota para el manejo de tarjetas de crédito.
public interface CreditCardInterface extends Remote {

    // Retorna el número de la tarjeta
    public String getCardNumber() throws Exception;

    // Retorna el código CVV de la tarjeta
    public String getCVV() throws Exception;

    // Retorna el nombre del titular
    public String getTitular() throws Exception;

    // Retorna el mes de expiración
    public int getExpirationMonth() throws Exception;

    // Retorna el año de expiración
    public int getExpirationYear() throws Exception;

    // Retorna el saldo disponible de la tarjeta
    public double getBalance() throws Exception;

    // Permite usar saldo de la tarjeta
    public CreditCard useCreditCard(double amount) throws Exception;

    // Permite realizar un pago a la tarjeta
    public CreditCard payCreditCard(double amount) throws Exception;

    // Retorna toda la información de la tarjeta en formato texto
    public String print() throws Exception;
}