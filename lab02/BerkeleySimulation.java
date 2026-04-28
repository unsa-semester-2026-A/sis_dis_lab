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

public class BerkeleySimulation {
    public static void main(String[] args) throws Exception {
        List<ClockNode> nodes = List.of(
            new ClockNode("M", 0.0),
            new ClockNode("E1", 5.2),
            new ClockNode("E2", -3.4),
            new ClockNode("E3", 500.0)
        );

        runBerkeley(nodes, 10.0);
    }

    static void runBerkeley(List<ClockNode> nodes, double threshold)throws Exception  {
        double master = nodes.get(0).readTime();

        List<Double> diffs = new ArrayList<>();
        List<Double> valid = new ArrayList<>();

        // calcular diferencias
        for (int i = 0; i < nodes.size(); i++) {
            double diff = (i == 0) ? 0 : nodes.get(i).readTime() - master;
            diffs.add(diff);

            if (Math.abs(diff) <= threshold) {
                valid.add(diff);
            }
        }

        double avg = valid.stream().mapToDouble(d -> d).average().orElse(0);

        // aplicar ajustes (pendiente mejorar lógica)
        for (int i = 0; i < nodes.size(); i++) {
            if (Math.abs(diffs.get(i)) <= threshold) {
                double offset = avg - diffs.get(i);
                nodes.get(i).adjust(offset);
            }
        }
    }
}