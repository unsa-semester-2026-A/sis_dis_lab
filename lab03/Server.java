import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

// el servidor que puede ejecutarse desde consola
public class Server {

  // un ID unico para cada conexion
  private static int uniqueId;

  // un ArrayList para mantener la lista de Clientes
  private ArrayList<ClientThread> al;

  // para mostrar la hora
  private SimpleDateFormat sdf;

  // numero de puerto para escuchar conexiones
  private int port;

  // para verificar si el servidor esta en ejecucion
  private boolean keepGoing;

  // notificacion
  private String notif = " *** ";

  // constructor que recibe el puerto para escuchar conexiones como parametro
  public Server(int port) {
    // el puerto
    this.port = port;

    // para mostrar hh:mm:ss
    sdf = new SimpleDateFormat("HH:mm:ss");

    // un ArrayList para mantener la lista de Clientes
    al = new ArrayList<ClientThread>();
  }

  public void start() {
    keepGoing = true;

    // crear socket de servidor y esperar solicitudes de conexion
    try {

      // el socket usado por el servidor
      ServerSocket serverSocket = new ServerSocket(port);

      // bucle infinito para esperar conexiones (mientras el servidor este activo)
      while (keepGoing) {
        display("Server waiting for Clients on port " + port + ".");

        // aceptar conexion si el cliente la solicita
        Socket socket = serverSocket.accept();

        // salir si el servidor se detuvo
        if (!keepGoing)
          break;

        // si el cliente esta conectado, crear su hilo
        ClientThread t = new ClientThread(socket);

        // agregar este cliente al arraylist
        al.add(t);
        t.start();
      }

      // intentar detener el servidor
      try {
        serverSocket.close();

        for (int i = 0; i < al.size(); ++i) {
          ClientThread tc = al.get(i);
          try {
            // cerrar todos los flujos de datos y el socket
            tc.sInput.close();
            tc.sOutput.close();
            tc.socket.close();
          } catch (IOException ioE) {
          }
        }
      } catch (Exception e) {
        display("Exception closing the server and clients: " + e);
      }

    } catch (IOException e) {
      String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
      display(msg);
    }
  }

  // para detener el servidor
  protected void stop() {
    keepGoing = false;
    try {
      new Socket("localhost", port);
    } catch (Exception e) {
    }
  }

  // mostrar un evento en la consola
  private void display(String msg) {
    String time = sdf.format(new Date()) + " " + msg;
    System.out.println(time);
  }

  // para enviar un mensaje a todos los Clientes
  private synchronized boolean broadcast(String message) {

    // agregar marca de tiempo al mensaje
    String time = sdf.format(new Date());

    // revisar si el mensaje es privado, es decir, de cliente a cliente
    String[] w = message.split(" ", 3);
    boolean isPrivate = false;

    if (w[1].charAt(0) == '@')
      isPrivate = true;

    // si es privado, enviar solo al nombre de usuario mencionado
    if (isPrivate == true) {

      String tocheck = w[1].substring(1, w[1].length());
      message = w[0] + w[2];

      String messageLf = time + " " + message + "\n";
      boolean found = false;

      // recorrer en orden inverso para encontrar el usuario mencionado
      for (int y = al.size(); --y >= 0;) {
        ClientThread ct1 = al.get(y);
        String check = ct1.getUsername();

        if (check.equals(tocheck)) {

          // intentar escribir al Cliente, si falla quitarlo de la lista
          if (!ct1.writeMsg(messageLf)) {
            al.remove(y);
            display("Disconnected Client " + ct1.username + " removed from list.");
          }

          // usuario encontrado y mensaje entregado
          found = true;
          break;
        }
      }

      // usuario mencionado no encontrado, devolver false
      if (found != true) {
        return false;
      }
    }

    // si el mensaje es de difusion
    else {
      String messageLf = time + " " + message + "\n";

      // mostrar mensaje
      System.out.print(messageLf);

      // recorrer en orden inverso por si hay que eliminar un Cliente
      // porque se desconecto
      for (int i = al.size(); --i >= 0;) {
        ClientThread ct = al.get(i);

        // intentar escribir al Cliente, si falla quitarlo de la lista
        if (!ct.writeMsg(messageLf)) {
          al.remove(i);
          display("Disconnected Client " + ct.username + " removed from list.");
        }
      }
    }
    return true;
  }

  // si el cliente envio LOGOUT para salir
  synchronized void remove(int id) {
    String disconnectedClient = "";

    // recorrer la lista hasta encontrar el Id
    for (int i = 0; i < al.size(); ++i) {
      ClientThread ct = al.get(i);

      // si se encuentra, eliminarlo
      if (ct.id == id) {
        disconnectedClient = ct.getUsername();
        al.remove(i);
        break;
      }
    }

    broadcast(notif + disconnectedClient + " has left the chat room." + notif);
  }

  /*
  * Para ejecutar como aplicacion de consola
  * > java Server
  * > java Server portNumber
  * Si no se especifica el numero de puerto se usa 1500
  */
  public static void main(String[] args) {

    // iniciar servidor en el puerto 1500 salvo que se indique otro PortNumber
    int portNumber = 1500;

    switch (args.length) {
      case 1:
        try {
          portNumber = Integer.parseInt(args[0]);
        } catch (Exception e) {
          System.out.println("Invalid port number.");
          System.out.println("Usage is: > java Server [portNumber]");
          return;
        }

      case 0:
        break;

      default:
        System.out.println("Usage is: > java Server [portNumber]");
        return;
    }

    // crear un objeto server e iniciarlo
    Server server = new Server(portNumber);
    server.start();
  }

  // una instancia de este hilo se ejecuta por cada cliente
  class ClientThread extends Thread {

    // socket para recibir mensajes del cliente
    Socket socket;
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;

    // id unico (mas facil para desconectar)
    int id;

    // nombre de usuario del Cliente
    String username;

    // objeto mensaje para recibir el mensaje y su tipo
    ChatMessage cm;

    // marca de tiempo
    String date;
  }
}