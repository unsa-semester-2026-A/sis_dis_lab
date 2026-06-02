# Cliente SOAP en Python para consumir el servicio de calculadora de DneOnline.
import sys
try:
    from zeep import Client
except ImportError:
    print("Error: Se requiere la libreria zeep para ejecutar este script.")
    print("Instalela usando: pip install zeep")
    sys.exit(1)

def main():
    # URL del WSDL del servicio web de calculadora
    wsdl_url = 'http://www.dneonline.com/calculator.asmx?WSDL'
    
    print("Estableciendo conexion con el servicio SOAP: " + wsdl_url)
    try:
        # Creacion del cliente SOAP con zeep
        client = Client(wsdl_url)
        
        # Valores de prueba
        num1 = 5
        num2 = 8
        
        print("\n--- Ejecutando operaciones de prueba ---")
        
        # Operacion de Suma (Add)
        resultado_suma = client.service.Add(num1, num2)
        print("Suma: " + str(num1) + " + " + str(num2) + " = " + str(resultado_suma))
        
        # Operacion de Resta (Subtract)
        resultado_resta = client.service.Subtract(num1, num2)
        print("Resta: " + str(num1) + " - " + str(num2) + " = " + str(resultado_resta))
        
        # Operacion de Multiplicacion (Multiply)
        resultado_mult = client.service.Multiply(num1, num2)
        print("Multiplicacion: " + str(num1) + " * " + str(num2) + " = " + str(resultado_mult))
        
        # Operacion de Division (Divide)
        resultado_div = client.service.Divide(num1, num2)
        print("Division: " + str(num1) + " / " + str(num2) + " = " + str(resultado_div))
        
        print("\nPrueba de consumo completada con exito.")
        
    except Exception as e:
        print("Ocurrio un error al interactuar con el servicio SOAP:")
        print(str(e))

if __name__ == '__main__':
    main()
