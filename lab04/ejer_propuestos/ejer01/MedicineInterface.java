import java.rmi.Remote;

// Interfaz remota para el manejo de medicamentos
public interface MedicineInterface extends Remote {

    // Retorna un objeto Medicine según la cantidad solicitada
    public Medicine getMedicine(int amount) throws Exception;

    // Retorna el stock disponible del medicamento
    public int getStock() throws Exception;

    // Retorna la información del medicamento en formato String
    public String print() throws Exception;
}