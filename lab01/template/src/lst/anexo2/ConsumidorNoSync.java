public class ConsumidorNoSync extends Thread {
    private CubbyHoleNoSync cubbyhole;
    private int numero;

    public ConsumidorNoSync(CubbyHoleNoSync c, int numero) {
        cubbyhole = c;
        this.numero = numero;
    }

    public void run() {
        int value = 0;
        for (int i = 0; i < 10; i++) {
            value = cubbyhole.get();
            System.out.println("Consumidor #" + this.numero + " obtiene: " + value);
        }
    }
}
