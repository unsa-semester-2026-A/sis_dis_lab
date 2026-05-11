import java.rmi.Remote;
import java.util.*;

// Interfaz remota para el manejo del stock de medicamentos
public interface StockInterface extends Remote {

    // Retorna un HashMap con todos los productos del stock
    public HashMap<String, MedicineInterface> getStockProducts() throws Exception;

    // Agrega un nuevo medicamento al stock
    public void addMedicine(String name, float price, int stock) throws Exception;

    // Permite comprar cierta cantidad de un medicamento
    public MedicineInterface buyMedicine(String name, int amount) throws Exception;
}