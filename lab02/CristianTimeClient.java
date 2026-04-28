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

        double t1 = System.currentTimeMillis() / 1000.0;
        double rtt = t1 - t0;
        double synchronizedTime = serverTime + (rtt / 2.0);
        double offset = synchronizedTime - t1;

        System.out.println("Tiempo local T1        : " + FMT.format(Instant.ofEpochMilli((long) (t1 * 1000))));
        System.out.println("Tiempo servidor Ts     : " + FMT.format(Instant.ofEpochMilli((long) (serverTime * 1000))));
        System.out.printf("RTT calculado          : %.6f s%n", rtt);
        System.out.println("Tiempo sincronizado    : " + FMT.format(Instant.ofEpochMilli((long) (synchronizedTime * 1000))));
        System.out.printf("Offset aplicado        : %+,.6f s%n", offset);
    }
}
