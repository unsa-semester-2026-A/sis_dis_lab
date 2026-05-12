import java.rmi.Naming;
import java.util.Scanner;

// Permite interactuar con el sistema de cambio de moneda mediante Java RMI
public class ClientSide {

    public static void main(String[] args)
    throws Exception {

        // Objeto Scanner para leer datos desde teclado
        Scanner sc = new Scanner(System.in);

        // Variable de control para mantener activo el menú
        boolean flag = true;

        // Bucle principal del programa
        while (flag) {

            // Conexión con el objeto remoto del servidor
            CurrencyExchangeInterface exchanger =
            (CurrencyExchangeInterface)
            Naming.lookup(
                "rmi://localhost:1099/Exchanger"
            );

            // Menú principal
            System.out.println(
                "\nIngresa la opción\n" +
                "1. Ver cambios actuales\n" +
                "2. Establecer cambio a dólares\n" +
                "3. Establecer cambio a euros\n" +
                "4. Hacer cambio a dolares\n" +
                "5. Hacer cambio a euros\n" +
                "6. Salir"
            );

            // Lee la opción ingresada
            int selection = sc.nextInt();

            // Opción 1: Mostrar tipos de cambio actuales
            if (selection == 1) {

                System.out.println(
                    "Cambio de dólares actual: " +
                    exchanger.getExchangeDolares()
                );

                System.out.println(
                    "Cambio de euros actual: " +
                    exchanger.getExchangeEuros()
                );
            }

            // Opción 2: Modificar el cambio del dólar
            else if (selection == 2) {

                System.out.println(
                    "Ingrese el nuevo cambio a dólares: "
                );

                double dolares = sc.nextDouble();

                // Actualiza el tipo de cambio
                exchanger.setExchangeDolares(dolares);

                System.out.println(
                    "El nuevo cambio a dólares es: " +
                    dolares
                );
            }

            // Opción 3: Modificar el cambio del euro
            else if (selection == 3) {

                System.out.println(
                    "Ingrese el nuevo cambio a euros: "
                );

                double euros = sc.nextDouble();

                // Actualiza el tipo de cambio
                exchanger.setExchangeEuros(euros);
            }

            // Opción 4: Convertir soles a dólares
            else if (selection == 4) {

                System.out.println(
                    "Ingrese monto (soles) para cambiar a dolares: "
                );

                double soles = sc.nextDouble();

                // Realiza la conversión
                double cambio =
                exchanger.exchangeDolares(soles);

                System.out.println(
                    "Su dinero en dólares es: " + cambio
                );
            }

            // Opción 5: Convertir soles a euros
            else if (selection == 5) {

                System.out.println(
                    "Ingrese monto (soles) para cambiar a euros: "
                );

                double soles = sc.nextDouble();

                // Realiza la conversión
                double cambio =
                exchanger.exchangeEuros(soles);

                System.out.println(
                    "Su dinero en euros es: " + cambio
                );
            }

            // Opción 6: Salir del sistema
            else if (selection == 6) {

                flag = false;

                System.out.println("Adiós");
            }

            // Opción inválida
            else {

                System.out.println(
                    "Seleccione una opcion valida"
                );
            }
        }

        // Cierra el Scanner
        sc.close();
    }
}