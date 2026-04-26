package lab02;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CristianTimeClient {
    private static final DateTimeFormatter FMT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            .withZone(ZoneId.systemDefault());

    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 8000;

        double t0 = System.currentTimeMillis() / 1000.0;
        double serverTime;

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            serverTime = Double.parseDouble(in.readLine());
        }
        //Aun falta...
    }
}