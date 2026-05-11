import java.rmi.*;

// Clase principal del servidor
public class ServerSide {

    public static void main(String[] args) throws Exception {

        // Se crea el objeto Stock que actuará como servidor remoto
        Stock pharmacy = new Stock();

        // Se agregan medicamentos iniciales al sistema.
        pharmacy.addMedicine("Paracetamol", 3.2f, 10);
        pharmacy.addMedicine("Mejoral", 2.0f, 20);
        pharmacy.addMedicine("Amoxilina", 1.0f, 30);
        pharmacy.addMedicine("Aspirina", 5.0f, 40);

        // Se registra el objeto remoto con el nombre "PHARMACY"
        // Los clientes podrán acceder a él usando este nombre
        Naming.rebind("PHARMACY", pharmacy);

        // Mensaje que indica que el servidor está listo
        System.out.println("Server ready");
    }
}