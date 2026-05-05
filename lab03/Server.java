/*Broadcast: Descomentar
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


*/