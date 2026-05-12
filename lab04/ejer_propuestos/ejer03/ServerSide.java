import java.rmi.*;

// Se encarga de crear y registrar el objeto remoto del sistema de cambio de monedas
public class ServerSide {

    // Constructor de la clase servidor
    public ServerSide() {

        try {

            // Crea el objeto remoto
            CurrencyExchangeInterface exchanger =
            new CurrencyExchange();

            // Registra el objeto remoto en el RMI Registry
            // El cliente podrá acceder usando la URL indicada
            Naming.rebind(
                "rmi://localhost:1099/Exchanger",
                exchanger
            );

            // Mensaje de confirmación
            System.out.println("Server Ready");

        } catch (Exception e) {

            // Muestra cualquier error ocurrido
            System.out.println("Trouble: " + e);
        }
    }

    // Método principal del programa
    public static void main(String[] args)
    throws Exception {

        // Crea una instancia del servidor
        new ServerSide();
    }
}