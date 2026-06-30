#!/bin/bash
# Automatización de generación de Certificados SSL/TLS con OpenSSL
# LogiMarket Perú — Laboratorio 11

set -e

# Crear directorio para almacenar los certificados si no existe
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
mkdir -p "$DIR/certs"
cd "$DIR/certs"

echo "============================================================"
echo "    PASO 1: Generación de la Entidad Certificadora Raíz (CA) "
echo "============================================================"

# 1. Generar la llave privada de la CA (RSA 4096 bits)
openssl genrsa -out ca.key 4096

# 2. Generar el certificado autofirmado de la CA
openssl req -x509 -new -nodes -key ca.key -sha256 -days 365 \
  -subj "/C=PE/ST=Lima/L=Lima/O=LogiMarket Peru/OU=Seguridad TI/CN=LogiMarket Root CA" \
  -out ca.crt

echo "[✓] CA Raíz generada correctamente: ca.key y ca.crt"
echo ""

echo "============================================================"
echo "    PASO 2: Generación de la Llave y CSR del Servidor"
echo "============================================================"

# 1. Generar la llave privada del servidor (RSA 2048 bits)
openssl genrsa -out server.key 2048

# 2. Configurar extensiones para Subject Alternative Name (SAN)
# Esto es crítico, ya que las librerías modernas (como Python 'ssl' o navegadores)
# exigen que el host esté especificado en el SAN y no solo en el Common Name (CN).
cat > server.ext << EOF
authorityKeyIdentifier=keyid,issuer
basicConstraints=CA:FALSE
keyUsage = digitalSignature, nonRepudiation, keyEncipherment, dataEncipherment
subjectAltName = @alt_names

[alt_names]
DNS.1 = logimarket.local
DNS.2 = localhost
IP.1 = 127.0.0.1
EOF

# 3. Generar la Solicitud de Firma de Certificado (CSR)
openssl req -new -key server.key \
  -subj "/C=PE/ST=Lima/L=Lima/O=LogiMarket Peru/OU=Operaciones IT/CN=logimarket.local" \
  -out server.csr

echo "[✓] Llave de servidor y CSR generados: server.key y server.csr"
echo ""

echo "============================================================"
echo "    PASO 3: Firma del Certificado del Servidor por la CA"
echo "============================================================"

# Firmar el certificado usando la CA y la configuración de extensiones SAN
openssl x509 -req -in server.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out server.crt -days 365 -sha256 -extfile server.ext

echo "[✓] Certificado de Servidor firmado exitosamente: server.crt"
echo ""

echo "============================================================"
echo "    PASO 4: Verificación de la Cadena de Confianza"
echo "============================================================"

# Ejecutar verificación de la cadena de certificados
openssl verify -CAfile ca.crt server.crt

echo "[✓] Verificación completada."
rm -f server.ext server.csr ca.srl
