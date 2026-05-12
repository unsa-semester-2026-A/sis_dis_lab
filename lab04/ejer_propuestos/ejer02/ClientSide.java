import java.rmi.Naming;
import java.util.HashMap;
import java.util.Scanner;

// Clase principal del cliente
public class ClientSide {

    public static void main(String[] args) throws Exception {

        // Objeto Scanner para leer datos desde teclado
        Scanner sc = new Scanner(System.in);

        // Variable de control del menú
        boolean flag = true;

        // Bucle principal del programa
        while (flag) {

            // Conexión con el objeto remoto registrado como "BCP"
            BankInterface bcp =
            (BankInterface) Naming.lookup("BCP");

            // Menú principal.
            System.out.println(
                "Ingresa la opción\n" +
                "1. Ver tarjetas\n" +
                "2. Crear tarjeta\n" +
                "3. Usar tarjeta\n" +
                "4. Pagar tarjeta\n" +
                "5. Salir\n"
            );

            // Lee la opción ingresada
            int selection = sc.nextInt();

            // Opción 1: Mostrar tarjetas registradas
            if (selection == 1) {

                // Obtiene todas las tarjetas almacenadas
                HashMap<String, CreditCardInterface> aux =
                (HashMap<String, CreditCardInterface>)
                bcp.getCreditCards();

                // Recorre todas las tarjetas
                for (String key : aux.keySet()) {

                    // Obtiene cada tarjeta
                    CreditCardInterface e =
                    (CreditCardInterface) aux.get(key);

                    // Muestra la información de la tarjeta
                    System.out.println(e.print());

                    // Separador visual
                    System.out.println(
                        "_________________________________"
                    );
                }
            }

            // Opción 2: Crear una nueva tarjeta
            else if (selection == 2) {

                // Solicita el número de tarjeta
                System.out.println(
                    "Ingresa el número de tarjeta: "
                );
                String cardNumber = sc.next();

                // Solicita el CVV
                System.out.println("Ingresa el CVV: ");
                String cvv = sc.next();

                // Solicita el titular
                System.out.println(
                    "Ingresa el nombre del titular: "
                );
                String titular = sc.next();

                // Solicita el mes de vencimiento
                System.out.println(
                    "Ingresa el mes de vencimiento: "
                );
                int expirationMonth = sc.nextInt();

                // Solicita el año de vencimiento
                System.out.println(
                    "Ingresa el año de vencimiento: "
                );
                int expirationYear = sc.nextInt();

                // Crea la tarjeta en el servidor
                bcp.addCreditCard(
                    cardNumber,
                    cvv,
                    titular,
                    expirationMonth,
                    expirationYear,
                    selection
                );

                // Mensaje de confirmación
                System.out.println(
                    "Usted acaba de crear su tarjeta"
                );
            }

            // Opción 3: Usar saldo de la tarjeta
            else if (selection == 3) {

                // Solicita el número de tarjeta
                System.out.println(
                    "Ingresa el número de tarjeta: "
                );
                String cardNumber = sc.next();

                // Solicita el CVV
                System.out.println("Ingresa el CVV: ");
                String cvv = sc.next();

                // Solicita el titular
                System.out.println(
                    "Ingresa el nombre del titular: "
                );
                String titular = sc.next();

                // Solicita el mes de vencimiento
                System.out.println(
                    "Ingresa el mes de vencimiento: "
                );
                int expirationMonth = sc.nextInt();

                // Solicita el año de vencimiento
                System.out.println(
                    "Ingresa el año de vencimiento: "
                );
                int expirationYear = sc.nextInt();

                // Solicita el monto a usar
                System.out.println(
                    "Ingresa el monto a usar: "
                );
                double amount = sc.nextDouble();

                // Realiza la operación en el servidor
                CreditCardInterface aux =
                bcp.useCreditCard(
                    cardNumber,
                    cvv,
                    titular,
                    expirationMonth,
                    expirationYear,
                    amount
                );

                // Muestra la información actualizada
                System.out.println(
                    "Usted acaba de usar su tarjeta"
                );

                System.out.println(aux.print());
            }

            // Opción 4: Pagar la tarjeta
            else if (selection == 4) {

                // Solicita el número de tarjeta
                System.out.println(
                    "Ingresa el número de tarjeta: "
                );
                String cardNumber = sc.next();

                // Solicita el monto a pagar
                System.out.println(
                    "Ingresa el monto a pagar: "
                );
                double amount = sc.nextDouble();

                // Realiza el pago
                CreditCardInterface aux =
                bcp.payCreditCard(cardNumber, amount);

                // Muestra la tarjeta actualizada
                System.out.println(
                    "Usted acaba de pagar su tarjeta"
                );

                System.out.println(aux.print());
            }

            // Opción 5: Salir del sistema
            else if (selection == 5) {

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