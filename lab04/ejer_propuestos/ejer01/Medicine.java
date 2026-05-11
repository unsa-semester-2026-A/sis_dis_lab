import java.rmi.server.UnicastRemoteObject;

/**
 * Clase que representa una medicina dentro del sistema.
 * Permite consultar información y realizar compras de medicamentos
 * 
 * @author rventurar
 */
public class Medicine extends UnicastRemoteObject implements MedicineInterface {

    // Nombre del medicamento
    private String name;

    // Precio unitario del medicamento
    private float unitPrice;

    // Cantidad disponible en stock
    private int stock;

    // Constructor vacío
    public Medicine() throws Exception {
        super();
    }

    // Constructor que inicializa los datos del medicamento
    public Medicine(String name, float price, int stock) throws Exception {
        super();

        this.name = name;
        this.unitPrice = price;
        this.stock = stock;
    }

    // Método que permite comprar cierta cantidad del medicamento
    @Override
    public Medicine getMedicine(int amount) throws Exception {

        // Verifica si el stock está vacío
        if (this.stock <= 0)
            throw new StockException("Stock empty");

        // Verifica si la cantidad solicitada supera el stock disponible
        if (this.stock - amount < 0)
            throw new StockException("Stock not amount of medicine");

        // Reduce el stock según la cantidad comprada
        this.stock -= amount;

        // Crea un nuevo objeto Medicine con el precio total de la compra
        Medicine aux = new Medicine(name, unitPrice * amount, stock);

        // Retorna el objeto creado
        return aux;
    }

    // Método que retorna el stock actual del medicamento
    @Override
    public int getStock() throws Exception {
        return this.stock;
    }

    // Método que retorna la información del medicamento en formato texto
    @Override
    public String print() throws Exception {

        return this.name +
               "\nPrice: " + this.unitPrice +
               "\nStock: " + this.stock;
    }
}