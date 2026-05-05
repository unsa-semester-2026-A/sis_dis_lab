import java.net.*;
import java.io.*;
import java.util.*;

// El Cliente que puede ejecutarse desde consola
public class Client {

  // notificacion
  private String notif = " *** ";

  // para E/S
  private ObjectInputStream sInput; // para leer desde el socket
  private ObjectOutputStream sOutput; // para escribir en el socket
  private Socket socket; // objeto socket

  private String server, username; // servidor y nombre de usuario
  private int port; // puerto

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /*
  * Constructor para definir lo siguiente
  * server: direccion del servidor
  * port: numero de puerto
  * username: nombre de usuario
  */
  Client(String server, int port, String username) {
    this.server = server;
    this.port = port;
    this.username = username;
  }

  /*
  * Para iniciar el chat
  */
  public boolean start() {

    // intentar conectarse al servidor
    try {
      socket = new Socket(server, port);
    }
    // manejar la excepcion si falla
    catch (Exception ec) {
      display("Error connectiong to server:" + ec);
      return false;
    }

    String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
    display(msg);

    /* Crear ambos flujos de datos */
    try {
      sInput = new ObjectInputStream(socket.getInputStream());
      sOutput = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException eIO) {
      display("Exception creating new Input/output Streams: " + eIO);
      return false;
    }

    // crear el hilo para escuchar al servidor
    new ListenFromServer().start();

    // enviar nuestro nombre de usuario al servidor, este es el unico mensaje
    // que se enviara como String. Todos los demas seran objetos ChatMessage
    try {
      sOutput.writeObject(username);
    } catch (IOException eIO) {
      display("Exception doing login : " + eIO);
      disconnect();
      return false;
    }

    // exito, informar al llamador que funciono
    return true;
  }

  /*
  * Para enviar un mensaje a la consola
  */
  private void display(String msg) {
    System.out.println(msg);
  }

  /*
  * Para enviar un mensaje al servidor
  */
  void sendMessage(ChatMessage msg) {
    try {
      sOutput.writeObject(msg);
    } catch (IOException e) {
      display("Exception writing to server: " + e);
    }
  }

  /*
  * Cuando algo sale mal
  * Cerrar los flujos de entrada/salida y desconectar
  */
  private void disconnect() {

    try {
      if (sInput != null) sInput.close();
    } catch (Exception e) {}

    try {
      if (sOutput != null) sOutput.close();
    } catch (Exception e) {}

    try {
      if (socket != null) socket.close();
    } catch (Exception e) {}
  }

  /*
  * Para iniciar el Cliente en modo consola usa uno de los siguientes comandos
  * > java Client
  * > java Client username
  * > java Client username portNumber
  * > java Client username portNumber serverAddress
  * en la consola
  * Si no se especifica el portNumber se usa 1500
  * Si no se especifica el serverAddress se usa "localHost"
  * Si no se especifica el username se usa "Anonymous"
  */
  public static void main(String[] args) {

    // valores por defecto si no se ingresan
    int portNumber = 1500;
    String serverAddress = "localhost";
    String userName = "Anonymous";

    Scanner scan = new Scanner(System.in);

    System.out.println("Enter the username: ");
    userName = scan.nextLine();

    // casos segun la cantidad de argumentos
    switch (args.length) {

      case 3:
        // para > javac Client username portNumber serverAddr
        serverAddress = args[2];

      case 2:
        // para > javac Client username portNumber
        try {
          portNumber = Integer.parseInt(args[1]);
        } catch (Exception e) {
          System.out.println("Invalid port number.");
          System.out.println("Usage is: > java Client [username] [portNumber]\n[serverAddress]");
          return;
        }

      case 1:
        // para > javac Client username
        userName = args[0];

      case 0:
        // para > java Client
        break;

      // si la cantidad de argumentos es invalida
      default:
        System.out.println("Usage is: > java Client [username] [portNumber]\n[serverAddress]");
        return;
    }

    // crear el objeto Client
    Client client = new Client(serverAddress, portNumber, userName);

    // intentar conectar al servidor y salir si no conecta
    if (!client.start())
      return;

    System.out.println("\nHello.! Welcome to the chatroom.");
    System.out.println("Instructions:");
    System.out.println("1. Simply type the message to send broadcast to all active clients");
    System.out.println("2. Type '@username<space>yourmessage' without quotes to send message to desired client");
    System.out.println("3. Type 'WHOISIN' without quotes to see list of active clients");
    System.out.println("4. Type 'LOGOUT' without quotes to logoff from server");

    // bucle infinito para obtener la entrada del usuario
    while (true) {
      System.out.print("> ");

      // leer mensaje del usuario
      String msg = scan.nextLine();

      // salir si el mensaje es LOGOUT
      if (msg.equalsIgnoreCase("LOGOUT")) {
        client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
        break;
      }

      // mensaje para revisar quienes estan en el chat
      else if (msg.equalsIgnoreCase("WHOISIN")) {
        client.sendMessage(new ChatMessage(ChatMessage.WHOISIN, ""));
      }

      // mensaje de texto normal
      else {
        client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, msg));
      }
    }

    // cerrar recurso
    scan.close();

    // el cliente termino su trabajo. desconectar cliente
    client.disconnect();
  }

  /*
  * una clase que espera mensajes del servidor
  */
  class ListenFromServer extends Thread {

    public void run() {
      while (true) {
        try {
          // leer el mensaje desde el flujo de entrada
          String msg = (String) sInput.readObject();

          // imprimir el mensaje
          System.out.println(msg);
          System.out.print("> ");
        } catch (IOException e) {
          display(notif + "Server has closed the connection: " + e + notif);
          break;
        } catch (ClassNotFoundException e2) {
        }
      }
    }
  }
}
