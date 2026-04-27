import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

class ClockNode {
    private final String id;
    private double localTimeSec;
    private static final Random RANDOM = new Random();

    ClockNode(String id, double driftSec) {
        this.id = id;
        this.localTimeSec = System.currentTimeMillis() / 1000.0 + driftSec;
    }

    String id() {
        return id;
    }

    double readTime() throws InterruptedException {
        // Simula latencia variable en la lectura remota
        Thread.sleep(10 + RANDOM.nextInt(40));
        return localTimeSec;
    }

    void adjust(double offsetSec) {
        localTimeSec += offsetSec;
        System.out.printf(Locale.US,
                "Nodo %s | ajuste=%+8.4f s | nuevaHora=%.4f%n",
                id, offsetSec, localTimeSec);
    }
}

