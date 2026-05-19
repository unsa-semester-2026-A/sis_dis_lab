package rmi;

import java.rmi.Naming;

public class Client {
  public static void main(String[] args) {
    try {
      String host = System.getenv("SERVER_HOST");
      if (host == null || host.isEmpty()) host="localhost";

      Calculator stub = (Calculator) Naming.lookup("rmi://"+host+":1099/CalculatorService");

      double a = 10;
      double b = 5;

      System.out.println("Conectando a host en "+host);
      System.out.println("Llamando a métodos RMI con a=" + a + ", b=" + b);
      System.out.println("Multiplicación: " + stub.multiply(a, b));
      System.out.println("División: " + stub.divide(a, b));
      System.out.println("Potencia: " + stub.power(a, b));

    } catch (Exception e) {
      System.err.println("Excepción del cliente: " + e.toString());
      e.printStackTrace();
    }
  }
}
