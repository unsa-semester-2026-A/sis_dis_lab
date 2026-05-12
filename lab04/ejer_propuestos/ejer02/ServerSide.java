import java.rmi.*;

// Se encarga de crear y registrar el objeto remoto del banco
public class ServerSide {

    public static void main(String[] args) throws Exception {

        // Se crea el objeto remoto del banco
        Bank bcp = new Bank();

        // Se registra el objeto remoto con el nombre "BCP"
        // Los clientes podrán acceder usando este identificador
        Naming.rebind("BCP", bcp);

        // Mensaje que indica que el servidor está listo
        System.out.println("Server Ready");
    }
}