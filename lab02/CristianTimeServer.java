import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CristianTimeServer {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Cristian iniciado en puerto " + port);
            while (true) {
                Socket client = serverSocket.accept();
                // Simula procesamiento del servidor antes de responder
                Thread.sleep(50);
                double serverTimeSec = System.currentTimeMillis() / 1000.0;
                try (PrintWriter out = new PrintWriter(client.getOutputStream(), true)) {
                    out.println(serverTimeSec);
                }
                client.close();
            }
        }
    }
}
