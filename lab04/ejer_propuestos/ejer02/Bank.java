import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

// Clase que representa el banco
public class Bank extends UnicastRemoteObject implements BankInterface {

    // HashMap que almacena las tarjetas registradas
    private HashMap<String, CreditCardInterface> cards = new HashMap<>();

    // Constructor requerido para objetos remotos RMI
    public Bank() throws RemoteException {
        super();
    }

    // Retorna todas las tarjetas registradas
    @Override
    public HashMap<String, CreditCardInterface>
    getCreditCards() throws Exception {

        return this.cards;
    }

    // Método para agregar una nueva tarjeta al banco
    @Override
    public CreditCardInterface addCreditCard(
        String cardNumber,
        String cvv,
        String titular,
        int expirationMonth,
        int expirationYear,
        double balance
    ) throws Exception {

        // Crea una nueva tarjeta
        CreditCardInterface creditCard =
        new CreditCard(
            cardNumber,
            cvv,
            titular,
            expirationMonth,
            expirationYear
        );

        // Verifica si la tarjeta ya existe
        if (cards.containsKey(cardNumber)) {
            throw new Exception("La tarjeta ya existe");
        }

        // Guarda la tarjeta en el HashMap
        cards.put(cardNumber, creditCard);

        return creditCard;
    }

    // Método para realizar compras con la tarjeta
    @Override
    public CreditCardInterface useCreditCard(
        String cardNumber,
        String cvv,
        String titular,
        int expirationMonth,
        int expirationYear,
        double amount
    ) throws Exception {

        // Busca la tarjeta por número
        CreditCardInterface aux =
        cards.get(cardNumber);

        // Verifica si la tarjeta existe
        if (aux == null) {

            throw new Exception("Tarjeta no encontrada");
        }

        // Verifica que los datos sean correctos
        if (!verifyCreditCard(
                cardNumber,
                cvv,
                titular,
                expirationMonth,
                expirationYear,
                aux
            )) {

            throw new Exception(
                "Ha colocado datos incorrectos"
            );
        }

        // Descuenta el monto de la tarjeta
        CreditCardInterface updated =
        aux.useCreditCard(amount);

        return updated;
    }

    // Método para pagar saldo de la tarjeta
    @Override
    public CreditCardInterface payCreditCard(
        String cardNumber,
        double amount
    ) throws Exception {

        // Busca la tarjeta
        CreditCardInterface aux =
        cards.get(cardNumber);

        // Verifica si existe
        if (aux == null) {

            throw new Exception(
                "Ha colocado datos incorrectos"
            );
        }

        // Realiza el pago
        CreditCard updated =
        aux.payCreditCard(amount);

        return updated;
    }

    // Método que verifica si los datos ingresados coinciden con los datos reales de la tarjeta.
    @Override
    public boolean verifyCreditCard(
        String cardNumber,
        String cvv,
        String titular,
        int expirationMonth,
        int expirationYear,
        CreditCardInterface creditCard
    ) throws Exception {

        return
            creditCard.getCardNumber().equals(cardNumber) &&
            creditCard.getCVV().equals(cvv) &&
            creditCard.getTitular().equals(titular) &&
            creditCard.getExpirationMonth() == expirationMonth &&
            creditCard.getExpirationYear() == expirationYear;
    }
}