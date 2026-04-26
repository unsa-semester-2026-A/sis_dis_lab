public class ProductorNoSync extends Thread {
    private CubbyHoleNoSync cubbyhole;
    private int numero;

    public ProductorNoSync(CubbyHoleNoSync c, int numero) {
        cubbyhole = c;
        this.numero = numero;
    }

    public void run() {
        for (int i = 0; i < 10; i++) {
            cubbyhole.put(i);
            System.out.println("Productor #" + this.numero + " pone: " + i);
            try {
                sleep((int)(Math.random() * 100));
            } catch (InterruptedException e) {}
        }
    }
}
