#!/usr/bin/env python3
"""
Script de Prueba Automatizado para Seguridad en Comunicaciones (HTTPS TLS 1.3)
Levanta el servidor seguro en un hilo, ejecuta las pruebas con el cliente y apaga todo.
"""

import sys
import os
import time
import threading

sys.path.append(os.path.dirname(os.path.abspath(__file__)))
import secure_server
import secure_client

def start_server():
    try:
        secure_server.main()
    except SystemExit:
        pass

def run_test():
    print("=" * 80)
    print("       INICIO DE PRUEBAS AUTOMATIZADAS DE SEGURIDAD - ACTIVIDAD 3        ")
    print("=" * 80)
    
    # 1. Verificar certificados
    dir_path = os.path.dirname(os.path.abspath(__file__))
    cert_file = os.path.join(dir_path, "certs", "server.crt")
    
    if not os.path.exists(cert_file):
        print("Error: Certificados no encontrados. Generando...")
        import subprocess
        subprocess.run(["./generate_certs.sh"], cwd=dir_path)
    
    # 2. Levantar el servidor en un hilo secundario daemon
    print("\n[Test] Iniciando servidor HTTPS seguro en segundo plano...")
    server_thread = threading.Thread(target=start_server, daemon=True)
    server_thread.start()
    
    # Esperar a que el servidor inicialice
    time.sleep(1.5)
    
    # 3. Ejecutar las pruebas del cliente
    print("\n[Test] Iniciando pruebas de conexion con secure_client...")
    # Prueba 1: Conexión sin CA (Debe fallar)
    secure_client.test_secure_connection(use_ca=False)
    
    print("\n")
    
    # Prueba 2: Conexión con CA (Debe tener éxito)
    secure_client.test_secure_connection(use_ca=True)
    
    print("\n" + "=" * 80)
    print("                      FIN DE PRUEBAS - APAGANDO SERVIDOR")
    print("=" * 80)

if __name__ == "__main__":
    run_test()
