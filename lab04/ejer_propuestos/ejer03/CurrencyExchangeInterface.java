import java.rmi.Remote;

// Interfaz remota para el sistema de cambio de monedas
public interface CurrencyExchangeInterface extends Remote {

    // Establece el tipo de cambio del dólar
    public void setExchangeDolares(double dolares)
    throws Exception;

    // Establece el tipo de cambio del euro
    public void setExchangeEuros(double euros)
    throws Exception;

    // Retorna el tipo de cambio actual del dólar
    public double getExchangeDolares()
    throws Exception;

    // Retorna el tipo de cambio actual del euro
    public double getExchangeEuros()
    throws Exception;

    // Convierte una cantidad en soles a dólares
    public double exchangeDolares(double soles)
    throws Exception;

    // Convierte una cantidad en soles a euros
    public double exchangeEuros(double soles)
    throws Exception;
}