import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

// Clase que implementa la interfaz remota StockInterface
public class Stock extends UnicastRemoteObject implements StockInterface {

    // HashMap que almacena los medicamentos disponibles
    private HashMap<String, MedicineInterface> medicines = new HashMap<>();

    // Constructor de la clase Stock
    // Lanza RemoteException porque trabaja con objetos remotos
    public Stock() throws RemoteException {
        super();
    }

    // Método para agregar un medicamento al stock
    @Override
    public void addMedicine(String name, float price, int stock) throws Exception {

        // Se crea un nuevo medicamento y se almacena en el HashMap
        medicines.put(name, new Medicine(name, price, stock));
    }

    // Método para comprar cierta cantidad de un medicamento
    @Override
    public MedicineInterface buyMedicine(String name, int amount) throws Exception {

        // Busca el medicamento por su nombre
        MedicineInterface aux = medicines.get(name);

        // Verifica si el medicamento existe
        if (aux == null) {

            // Lanza una excepción si no se encuentra
            throw new Exception("Impossible to find " + name);
        }

        // Obtiene el medicamento con la cantidad solicitada
        MedicineInterface element = aux.getMedicine(amount);

        // Retorna el medicamento actualizado
        return element;
    }

    // Método que retorna todos los medicamentos del stock
    @Override
    public HashMap<String, MedicineInterface> getStockProducts() throws Exception {

        // Devuelve el HashMap completo
        return this.medicines;
    }
}