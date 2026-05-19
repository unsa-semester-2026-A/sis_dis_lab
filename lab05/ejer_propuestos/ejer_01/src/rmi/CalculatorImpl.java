package rmi;

import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;

public class CalculatorImpl extends UnicastRemoteObject implements Calculator {

  protected CalculatorImpl() throws RemoteException {
    super();
  }

  private void logRequest(String method, double a, double b) {
    try {
      String clientHost = RemoteServer.getClientHost();
      System.out.println("[log] petición desde " + clientHost + " método: " + method + "("+a+","+b+")");
    } catch ( ServerNotActiveException e) {
      System.out.println("[LOG] Petición local -> " + method);
    }
  }

  @Override
  public double multiply(double a, double b) throws RemoteException {
    logRequest("multiply", a, b);
    return a * b;
  }

  @Override
  public double divide(double a, double b) throws RemoteException {
    logRequest("divide", a, b);
    if (b == 0) {
      throw new RemoteException("División por cero");
    }
    return a / b;
  }

  @Override
  public double power(double a, double b) throws RemoteException {
    logRequest("power", a, b);
    return Math.pow(a, b);
  }
}
