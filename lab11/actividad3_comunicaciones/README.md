# Actividad 3 — Seguridad en Comunicaciones (OpenSSL + TLS 1.3)

Este directorio contiene el script de generación de certificados SSL y los scripts cliente/servidor que demuestran la implementación de seguridad en tránsito mediante TLS 1.3 para LogiMarket Perú.

## Contenido

1. **`generate_certs.sh`**: Script en Bash que automatiza el proceso de creación de una CA (Entidad Certificadora) interna, la generación de la llave privada del servidor, la solicitud de firma (CSR) con soporte SAN (Subject Alternative Name) para `logimarket.local`, y la firma final del certificado.
2. **`secure_server.py`**: Servidor HTTPS mínimo escrito en Python que fuerza el uso de TLS 1.3 y cabeceras de seguridad como HSTS.
3. **`secure_client.py`**: Cliente HTTPS en Python para verificar el handshake TLS, comprobar la suite de cifrado y validar la autenticidad de la cadena de certificados.
4. **`certs/`**: Directorio contenedor de los certificados criptográficos generados.

---

## Resultados y Evidencias de la Verificación

### 1. Generación de Certificados
Al ejecutar `./generate_certs.sh`, se produce la CA raíz (`ca.crt`) y el certificado de servidor (`server.crt`). La validación del certificado por parte de OpenSSL indica:
```
server.crt: OK
[✓] Verificación completada.
```

### 2. Prueba de Seguridad en Tránsito (Handshake TLS 1.3)
La prueba simula una conexión cliente-servidor bajo dos escenarios:

#### Escenario A: Conexión sin registrar la CA Raíz (Fallo de Confianza)
Cuando un cliente intenta conectarse sin conocer la CA firmante (comportamiento de navegadores ante certificados inválidos o ataques MitM), la librería SSL interrumpe la conexión inmediatamente por seguridad:
```
----------------------------------------------------------------------
[!] Conectando SIN Validación de CA Raíz (Esperando error de confianza)
----------------------------------------------------------------------
[✗] Error de conexión: <urlopen error [SSL: CERTIFICATE_VERIFY_FAILED] certificate verify failed: unable to get local issuer certificate (_ssl.c:1000)>
```
*Esto demuestra la efectividad de la protección criptográfica ante intentos de interceptación de tráfico (Incidente 2).*

#### Escenario B: Conexión con Validación de CA Raíz (Conexión Exitosa)
Al cargar la CA de LogiMarket en el almacén de confianza del cliente, la conexión se establece de forma segura utilizando los estándares más exigentes del sector:
```
----------------------------------------------------------------------
[+] Conectando CON Validación de CA Raíz: /home/alvaro9rqc/.../certs/ca.crt
----------------------------------------------------------------------
[✓] CONEXION EXITOSA de extremo a extremo!
  - Servidor: https://localhost:8443
  - Version TLS negociada: TLSv1.3
  - Suite de Cifrado: TLS_AES_256_GCM_SHA384
  - Payload Recibido:
{
    "status": "CONEXION_SEGURA_OK",
    "mensaje": "Bienvenido al canal seguro de comunicaciones de LogiMarket Peru",
    "detalles_tls": {
        "version_protocolo": "TLSv1.3",
        "cipher_suite": "TLS_AES_256_GCM_SHA384",
        "bits_seguridad": 256
    },
    "activo_protegido": "Gateway de Pagos & API de Transacciones"
}
```

---

## Cómo Ejecutar las Pruebas

1. Genere los certificados locales:
   ```bash
   ./generate_certs.sh
   ```
2. Inicie el servidor HTTPS en una terminal:
   ```bash
   python3 secure_server.py
   ```
3. En otra terminal, ejecute el cliente de prueba:
   ```bash
   python3 secure_client.py
   ```
4. Detenga el servidor con `Ctrl+C` tras finalizar la verificación.

## Subida a Git
Este módulo y sus archivos son responsabilidad de:
- **Integrante:** Hancco Mullisaca, Sergio Danilo
