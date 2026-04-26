/**
 * CubbyHole con sincronizacion mediante wait/notify.
 * Garantiza que el Consumidor obtiene cada valor producido exactamente una vez.
 */
public class CubbyHole {
    private int contents;
    private boolean available = false;

    public synchronized int get() {
        // Espera hasta que haya un valor disponible
        while (available == false) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        available = false;
        notifyAll();
        return contents;
    }

    public synchronized void put(int value) {
        // Espera hasta que el valor anterior haya sido consumido
        while (available == true) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        contents = value;
        available = true;
        notifyAll();
    }
}
