/**
 * CubbyHole SIN sincronizacion.
 * Demuestra las condiciones de carrera (race conditions)
 * cuando dos hilos acceden a un recurso compartido sin control.
 */
public class CubbyHoleNoSync {
    private int contents;

    public int get() {
        return contents;
    }

    public void put(int value) {
        contents = value;
    }
}
