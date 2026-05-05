import java.io.*;
/*
* Esta clase define los diferentes tipos de mensajes que se intercambian entre
* los Clientes y el Servidor.
* Cuando se comunica un Cliente Java con un Servidor Java es mucho mas facil pasar objetos Java,
* no hay necesidad de contar bytes ni de esperar un salto de linea al final del frame
*/

public class ChatMessage implements Serializable {
  // Los diferentes tipos de mensaje enviados por el Cliente
  // WHOISIN para recibir la lista de usuarios conectados
  // MESSAGE es un mensaje de texto normal
  // LOGOUT para desconectarse del Servidor
  static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;
  private int type;
  private String message;

  // constructor
  ChatMessage(int type, String message) {
    this.type = type;
    this.message = message;
  }

  int getType() {
    return type;
  }
  
  String getMessage() {
    return message;
  }
}
