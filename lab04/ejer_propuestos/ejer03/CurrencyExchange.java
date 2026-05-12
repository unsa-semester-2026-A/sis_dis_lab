import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

// Clase que representa el sistema de cambio de monedas
public class CurrencyExchange
extends UnicastRemoteObject
implements CurrencyExchangeInterface {

    // Tipo de cambio del dólar
    private double exchangeDolares;

    // Tipo de cambio del euro
    private double exchangeEuros;

    // Constructor requerido para objetos remotos RMI con valores predeterminados
    public CurrencyExchange() throws RemoteException {
        super();
        this.exchangeDolares = 3.47;
        this.exchangeEuros = 4.08;
    }

    // Establece un nuevo tipo de cambio para dólares
    @Override
    public void setExchangeDolares(double dolares) throws Exception {
        if (dolares <= 0){
            throw new Exception("El cambio no puede ser negativo ni 0");
        }
        this.exchangeDolares = dolares;
    }

    // Establece un nuevo tipo de cambio para euros
    @Override
    public void setExchangeEuros(double euros) throws Exception {
        if (euros <= 0){
            throw new Exception("El cambio no puede ser negativo ni 0");
        }
        this.exchangeEuros = euros;
    }

    // Retorna el tipo de cambio actual del dólar
    @Override
    public double getExchangeDolares() throws Exception {

        return this.exchangeDolares;
    }

    // Retorna el tipo de cambio actual del euro
    @Override
    public double getExchangeEuros() throws Exception {

        return this.exchangeEuros;
    }

    // Convierte soles a dólares
    @Override
    public double exchangeDolares(double soles) throws Exception {

        // Realiza la conversión y redondea a 2 decimales
        double newSoles =
        Math.round(
            soles * 100 / this.exchangeDolares
        ) / 100.0;

        return newSoles;
    }

    // Convierte soles a euros
    @Override
    public double exchangeEuros(double soles) throws Exception {

        // Realiza la conversión y redondea a 2 decimales
        double newSoles =
        Math.round(
            soles * 100 / this.exchangeEuros
        ) / 100.0;

        return newSoles;
    }
}