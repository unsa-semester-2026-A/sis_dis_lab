import java.rmi.server.UnicastRemoteObject;

// Clase que representa una tarjeta de crédito
public class CreditCard extends UnicastRemoteObject implements CreditCardInterface {

    // Número de la tarjeta
    private String cardNumber;

    // Código de seguridad
    private String CVV;

    // Nombre del titular
    private String titular;

    // Mes de expiración
    private int expirationMonth;

    // Año de expiración
    private int expirationYear;

    // Saldo disponible de la tarjeta
    private double balance;

    // Constructor vacío requerido para RMI
    public CreditCard() throws Exception {
        super();
    }

    // Constructor principal de la tarjeta
    public CreditCard(
        String cardNumber,
        String CVV,
        String titular,
        int expirationMonth,
        int expirationYear
    ) throws Exception {

        // Verifica que los datos ingresados sean válidos
        if (!verifyData(
                cardNumber,
                CVV,
                titular,
                expirationMonth,
                expirationYear
            )) {

            throw new CreditCardException(
                "Datos inconsistentes, vuelva a intentarlo"
            );
        }

        // Inicialización de atributos
        this.CVV = CVV;
        this.cardNumber = cardNumber;
        this.expirationYear = expirationYear;
        this.expirationMonth = expirationMonth;
        this.titular = titular;

        // Saldo inicial por defecto
        this.balance = 1000.0;
    }

    // Constructor que permite definir el saldo manualmente
    public CreditCard(
        String cardNumber,
        String CVV,
        String titular,
        int expirationMonth,
        int expirationYear,
        double balance
    ) throws Exception {

        this.CVV = CVV;
        this.cardNumber = cardNumber;
        this.expirationYear = expirationYear;
        this.expirationMonth = expirationMonth;
        this.titular = titular;
        this.balance = balance;
    }

    // Retorna el número de tarjeta
    @Override
    public String getCardNumber() throws Exception {
        return this.cardNumber;
    }

    // Retorna el CVV
    @Override
    public String getCVV() throws Exception {
        return this.CVV;
    }

    // Retorna el titular de la tarjeta
    @Override
    public String getTitular() throws Exception {
        return this.titular;
    }

    // Retorna el mes de expiración
    @Override
    public int getExpirationMonth() throws Exception {
        return this.expirationMonth;
    }

    // Retorna el año de expiración
    @Override
    public int getExpirationYear() throws Exception {
        return this.expirationYear;
    }

    // Retorna el saldo actual
    @Override
    public double getBalance() throws Exception {
        return this.balance;
    }

    // Método para usar saldo de la tarjeta
    @Override
    public CreditCard useCreditCard(double amount) throws Exception {

        // Verifica si la tarjeta no tiene saldo
        if (this.balance <= 0) {
            throw new CreditCardException(
                "No hay saldo en la tarjeta, páguela."
            );
        }

        // Verifica si el monto excede el saldo disponible
        if (this.balance - amount < 0) {
            throw new CreditCardException(
                "No hay suficiente saldo en la tarjeta"
            );
        }

        // Descuenta el monto utilizado
        this.balance -= amount;

        // Retorna la tarjeta actualizada
        return this;
    }

    // Método para pagar la tarjeta
    @Override
    public CreditCard payCreditCard(double amount) throws Exception {

        // Verifica si la tarjeta ya está completamente pagada
        if (this.balance == 1000) {
            throw new CreditCardException(
                "Ya pagó la tarjeta"
            );
        }

        // Agrega el monto pagado al saldo
        this.balance += amount;

        // Evita que el saldo supere el límite máximo
        if (this.balance > 1000) {
            this.balance = 1000;
        }

        // Crea una nueva tarjeta actualizada
        CreditCard result = new CreditCard(
            this.cardNumber,
            this.CVV,
            this.titular,
            this.expirationMonth,
            this.expirationYear,
            this.balance
        );

        return result;
    }

    // Método que verifica si los datos de la tarjeta son válidos
    @SuppressWarnings("null")
    private boolean verifyData(
        String cardNumber,
        String CVV,
        String titular,
        int expirationMonth,
        int expirationYear
    ) throws CreditCardException {

        // Verifica que el número de tarjeta no sea null
        if (cardNumber == null) {
            throw new CreditCardException(
                "El número de tarjeta no puede ser null"
            );
        }

        // Verifica que tenga exactamente 16 dígitos
        if (cardNumber.length() != 16) {
            throw new CreditCardException(
                "El número de tarjeta debe tener 16 dígitos"
            );
        }

        // Verifica que solo contenga números
        if (!verifyString(cardNumber)) {
            throw new CreditCardException(
                "El número de tarjeta solo debe contener números"
            );
        }

        // Verifica que el CVV no sea null
        if (CVV == null) {
            throw new CreditCardException(
                "El CVV no puede ser null"
            );
        }

        // Verifica que el CVV tenga 3 dígitos
        if (CVV.length() != 3) {
            throw new CreditCardException(
                "El CVV debe tener 3 dígitos"
            );
        }

        // Verifica que el CVV solo tenga números
        if (!verifyString(CVV)) {
            throw new CreditCardException(
                "El CVV solo debe contener números"
            );
        }

        // Verifica que el titular no esté vacío
        if (titular == null || titular.isBlank()) {
            throw new CreditCardException(
                "El titular no puede estar vacío"
            );
        }

        // Verifica que el mes sea válido
        if (expirationMonth < 1 || expirationMonth > 12) {
            throw new CreditCardException(
                "Mes de expiración inválido"
            );
        }

        // Verifica que el año sea válido
        if (expirationYear < 0 || expirationYear > 99) {
            throw new CreditCardException(
                "Año de expiración inválido"
            );
        }

        return true;
    }

    // Método auxiliar que verifica si un String contiene únicamente números
    private boolean verifyString(String cardNumber) {

        for (int i = 0; i < cardNumber.length(); i++) {

            // Verifica carácter por carácter.
            if (!Character.isDigit(cardNumber.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    // Retorna la información de la tarjeta en formato texto
    @Override
    public String print() throws Exception {

        String creditCard = "";
        creditCard += "Número de tarjeta: " + this.cardNumber + "\n";
        creditCard += "Titular: " + this.titular + "\n";
        creditCard += "Mes y día de vencimiento: " + this.expirationMonth + "/" + this.expirationYear + "\n";
        creditCard += "CVV: " + this.CVV + "\n";
        creditCard += "Saldo de la tarjeta: " + this.balance + "\n";

        return creditCard;
    }
}