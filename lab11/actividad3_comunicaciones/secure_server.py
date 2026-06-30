#!/usr/bin/env python3
"""
Servidor HTTPS Seguro (TLS 1.3)
LogiMarket Perú — Laboratorio 11
"""

import http.server
import ssl
import os
import sys

PORT = 8443
HOST = 'localhost' # O '127.0.0.1'

class SecureRequestHandler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        # Desactivar listado de directorios y retornar respuesta JSON segura
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.send_header("Strict-Transport-Security", "max-age=63072000; includeSubDomains; preload") # HSTS
        self.send_header("X-Frame-Options", "DENY")
        self.send_header("X-Content-Type-Options", "nosniff")
        self.end_headers()
        
        # Obtener información del cifrado TLS utilizado
        cipher, version, secret_bits = self.request.cipher()
        
        response = {
            "status": "CONEXION_SEGURA_OK",
            "mensaje": "Bienvenido al canal seguro de comunicaciones de LogiMarket Peru",
            "detalles_tls": {
                "version_protocolo": version,
                "cipher_suite": cipher,
                "bits_seguridad": secret_bits
            },
            "activo_protegido": "Gateway de Pagos & API de Transacciones"
        }
        import json
        self.wfile.write(json.dumps(response, indent=2).encode('utf-8'))

def main():
    dir_path = os.path.dirname(os.path.realpath(__file__))
    cert_file = os.path.join(dir_path, "certs", "server.crt")
    key_file = os.path.join(dir_path, "certs", "server.key")
    
    if not os.path.exists(cert_file) or not os.path.exists(key_file):
        print(f"Error: No se encontraron los certificados en {cert_file} o {key_file}.")
        print("Por favor, ejecute primero el script './generate_certs.sh'.")
        sys.exit(1)
        
    server_address = (HOST, PORT)
    httpd = http.server.HTTPServer(server_address, SecureRequestHandler)
    
    # Configurar el contexto SSL de manera segura
    # ssl.PROTOCOL_TLS_SERVER configura un contexto por defecto optimizado para servidores con altos estándares
    context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
    context.load_cert_chain(certfile=cert_file, keyfile=key_file)
    
    # Restringir protocolos: Forzar TLS 1.3 como mínimo recomendado
    context.minimum_version = ssl.TLSVersion.TLSv1_3
    
    # Enlazar el socket SSL al servidor HTTP
    httpd.socket = context.wrap_socket(httpd.socket, server_side=True)
    
    print(f"\n[+] Servidor HTTPS seguro corriendo en https://{HOST}:{PORT}")
    print(f"[+] Forzando protocolo mínimo: TLSv1.3")
    print(f"[+] Certificado cargado: {cert_file}")
    print("[*] Presione Ctrl+C para detener el servidor...\n")
    
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\n[-] Apagando servidor seguro.")
        sys.exit(0)

if __name__ == "__main__":
    main()
