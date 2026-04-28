package lab02;
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
            new ClockNode("M(0)", 0.0),
            new ClockNode("E(1)", 5.2),
            new ClockNode("E(2)", -3.4),
            new ClockNode("E(3)", 500.0)
        );
        runBerkeley(nodes, 10.0);
    }

    static void runBerkeley(List<ClockNode> nodes, double thresholdSec) throws Exception {
        System.out.println("Ejecutando la simulacion de Berkeley ---");
        double masterTime = nodes.get(0).readTime();
        List<Double> diffs = new ArrayList<>();
        List<Boolean> validNode = new ArrayList<>();
        List<Double> validDiffs = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            ClockNode node = nodes.get(i);
            double diff = (i == 0) ? 0.0 : (node.readTime() - masterTime);
            diffs.add(diff);
            boolean isValid = Math.abs(diff) <= thresholdSec;
            validNode.add(isValid);
            if (isValid) {
                validDiffs.add(diff);
            }
            System.out.printf(Locale.US,
                    "Nodo %s | diferencia al maestro=%+8.4f s%n",
                    node.id(), diff);
        }

        double avg = validDiffs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        System.out.printf(Locale.US, "Promedio valido=%+8.4f s%n", avg);
        System.out.println("Aplicando offsets:");

        for (int i = 0; i < nodes.size(); i++) {
            if (!validNode.get(i)) {
                System.out.printf("Nodo %s | fuera de umbral, no se ajusta%n", nodes.get(i).id());
                continue;
            }
            double offset = avg - diffs.get(i);
            nodes.get(i).adjust(offset);
        }
    }
}
