// Demostracion SIN sincronizacion - evidencia condiciones de carrera
public class DemoNoSync {
    public static void main(String[] args) {
        CubbyHoleNoSync cub = new CubbyHoleNoSync();
        ConsumidorNoSync cons = new ConsumidorNoSync(cub, 1);
        ProductorNoSync prod = new ProductorNoSync(cub, 1);

        prod.start();
        cons.start();
    }
}
