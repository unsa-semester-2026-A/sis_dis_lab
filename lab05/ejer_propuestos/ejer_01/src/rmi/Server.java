package rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
  public static void main(String[] args) {
    try {
      // Iniciar el registro RMI en el puerto 1099
      LocateRegistry.createRegistry(1099);

      Calculator stub = new CalculatorImpl();

      // Enlazar el objeto remoto en el registro
      Naming.rebind("rmi://localhost:1099/CalculatorService", stub);

      System.out.println("Servidor RMI listo...");
    } catch (Exception e) {
      System.err.println("Excepción del servidor: " + e.toString());
      e.printStackTrace();
    }
  }
}
