#!/usr/bin/env python3
"""
Cliente HTTPS Seguro con Validación de Cadena de Confianza
LogiMarket Perú — Laboratorio 11
"""

import urllib.request
import ssl
import os
import sys
import json

PORT = 8443
HOST = 'localhost'

def test_secure_connection(use_ca=True):
    dir_path = os.path.dirname(os.path.realpath(__file__))
    ca_file = os.path.join(dir_path, "certs", "ca.crt")
    
    url = f"https://{HOST}:{PORT}"
    
    print("-" * 70)
    if use_ca:
        print(f"[+] Conectando CON Validación de CA Raíz: {ca_file}")
    else:
        print("[!] Conectando SIN Validación de CA Raíz (Esperando error de confianza)")
    print("-" * 70)
    
    try:
        # Configurar contexto SSL
        if use_ca:
            # Crear un contexto SSL por defecto para clientes (requiere verificación de host y cadena)
            context = ssl.create_default_context(cafile=ca_file)
            # Como nos conectamos a 'localhost' o '127.0.0.1' pero el certificado tiene esos nombres en SAN,
            # la validación de hostname funcionará correctamente.
        else:
            # Contexto por defecto del sistema (que no tiene cargada nuestra CA raíz personalizada)
            context = ssl.create_default_context()
            
        # Forzar protocolo mínimo TLS 1.3
        context.minimum_version = ssl.TLSVersion.TLSv1_3
        
        # Realizar la petición HTTP
        req = urllib.request.Request(url)
        with urllib.request.urlopen(req, context=context) as response:
            html = response.read().decode('utf-8')
            data = json.loads(html)
            
            print(f"\033[92m[✓] CONEXION EXITOSA de extremo a extremo!\033[0m")
            print(f"  - Servidor: {url}")
            print(f"  - Version TLS negociada: {data['detalles_tls']['version_protocolo']}")
            print(f"  - Suite de Cifrado: {data['detalles_tls']['cipher_suite']}")
            print(f"  - Payload Recibido:\n{json.dumps(data, indent=4)}")
            return True
            
    except ssl.SSLCertVerificationError as e:
        print(f"\033[91m[✗] ERROR DE VALIDACION SSL (Esperado): El certificado del servidor no es de confianza.\033[0m")
        print(f"  - Detalle del error: {e.reason}")
        return False
    except Exception as e:
        print(f"\033[91m[✗] Error de conexión: {e}\033[0m")
        return False

def main():
    print("=" * 70)
    print("      CLIENTE HTTPS SEGURO TLS 1.3 - PRUEBA DE CONFIANZA Y HANDSHAKE      ")
    print("=" * 70)
    
    # Prueba 1: Sin CA personalizada (debe fallar la validación porque es una CA autofirmada desconocida para el sistema)
    test_secure_connection(use_ca=False)
    
    print("\n")
    
    # Prueba 2: Con nuestra CA personalizada cargada en el cliente (debe tener éxito)
    test_secure_connection(use_ca=True)

if __name__ == "__main__":
    main()
