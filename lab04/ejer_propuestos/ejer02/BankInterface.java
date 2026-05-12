import java.rmi.Remote;
import java.util.*;

// Interfaz remota para el manejo de tarjetas de crédito en el banco
public interface BankInterface extends Remote {

    // Retorna todas las tarjetas registradas en el banco
    public HashMap<String, CreditCardInterface> getCreditCards() throws Exception;

    // Agrega una nueva tarjeta de crédito al sistema
    public CreditCardInterface addCreditCard(
        String cardNumber,
        String cvv,
        String titular,
        int expirationMonth,
        int expirationYear,
        double balance
    ) throws Exception;

    // Permite realizar una compra usando una tarjeta
    public CreditCardInterface useCreditCard(
        String cardNumber,
        String cvv,
        String titular,
        int expirationMonth,
        int expirationYear,
        double amount
    ) throws Exception;

    // Permite pagar una deuda de la tarjeta
    public CreditCardInterface payCreditCard(
        String cardNumber,
        double amount
    ) throws Exception;

    // Verifica que los datos ingresados coincidan con la tarjeta registrada en el sistema
    public boolean verifyCreditCard(
        String cardNumber,
        String cvv,
        String titular,
        int expirationMonth,
        int expirationYear,
        CreditCardInterface creditCard
    ) throws Exception;
}