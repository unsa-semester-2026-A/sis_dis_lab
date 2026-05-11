import java.rmi.Naming;
import java.util.HashMap;
import java.util.Scanner;

// Clase principal del cliente
public class ClienteSide {

    public static void main(String[] args) throws Exception {

        // Objeto Scanner para leer datos desde teclado
        Scanner sc = new Scanner(System.in);

        // Variable de control para mantener activo el menú
        boolean flag = true;

        // Se conecta al objeto remoto registrado como "PHARMACY"
        StockInterface pharm =
        (StockInterface) Naming.lookup("PHARMACY");

        // Bucle principal del programa
        while (flag) {

            // Menú de opciones.
            System.out.println(
                "Ingresa la opcion\n" +
                "1: Listar productos\n" +
                "2: Comprar Producto\n" +
                "3: Salir\n"
            );

            // Lee la opción seleccionada por el usuario
            int selection = sc.nextInt();

            // Opción 1: Mostrar productos disponibles
            if (selection == 1) {

                // Obtiene el HashMap con todos los medicamentos
                HashMap<String, MedicineInterface> aux =
                (HashMap<String, MedicineInterface>)
                pharm.getStockProducts();

                // Recorre todos los medicamentos almacenados
                for (String key : aux.keySet()) {

                    // Obtiene cada medicamento
                    MedicineInterface e =
                    (MedicineInterface) aux.get(key);

                    // Muestra la información del medicamento
                    System.out.println(e.print());

                    // Separador visual
                    System.out.println("*--------------*");
                }
            }

            // Opción 2: Comprar medicamento
            else if (selection == 2) {

                // Solicita el nombre del medicamento
                System.out.println("Ingrese nombre de la medicina");
                String medicine = sc.next();

                // Solicita la cantidad a comprar
                System.out.println("Ingrese cantidad a comprar");
                int amount = sc.nextInt();

                // Realiza la compra mediante el servidor remoto
                MedicineInterface aux =
                pharm.buyMedicine(medicine, amount);

                // Muestra la información de la compra
                System.out.println("Usted acaba de comprar");
                System.out.println(aux.print());
            }

            // Opción 3: Salir del programa
            else if (selection == 3) {

                flag = false;

                System.out.println("Adiós");
            }

            // Opción inválida
            else {

                System.out.println("Seleccione una opcion valida");
            }
        }

        // Cierra el Scanner
        sc.close();
    }
}