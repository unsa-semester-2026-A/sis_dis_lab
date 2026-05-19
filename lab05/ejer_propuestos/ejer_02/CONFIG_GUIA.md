# Guía de Configuración y Exposición

Esta guía te ayudará a configurar el proyecto para que sea dinámico utilizando variables de entorno y a lanzar la interfaz web interactiva.

## 1. Configuración de Variables de Entorno (.env)

Crea un archivo llamado `.env` en la raíz del proyecto con el siguiente contenido:

```env
# Configuración del Servidor gRPC
SERVER_PORT=50052

# Configuración del Cliente (para conectar al servidor)
GRPC_SERVER_ADDR=localhost:50052

# Configuración de la Interfaz Web
WEB_PORT=8080
```

### ¿Por qué usar .env?
- **Flexibilidad:** Permite cambiar puertos sin recompilar el código.
- **Seguridad:** Evita "hardcodear" direcciones IP o credenciales.
- **Portabilidad:** Facilita el despliegue en diferentes entornos (Docker vs Local).

## 2. Modificación del Código (Go)

Para leer estas variables, usaremos el paquete estándar `os` en Go.

### Servidor (`server/main.go`)
Busca la línea de `net.Listen` y cámbiala por:
```go
port := os.Getenv("SERVER_PORT")
if port == "" {
    port = "50052" // Valor por defecto
}
lis, err := net.Listen("tcp", ":" + port)
```

### Cliente (`client/main.go`)
Busca la línea de `grpc.Dial` y cámbiala por:
```go
addr := os.Getenv("GRPC_SERVER_ADDR")
if addr == "" {
    addr = "localhost:50052" // Valor por defecto
}
conn, err := grpc.Dial(addr, grpc.WithTransportCredentials(insecure.NewCredentials()))
```

## 3. Interfaz Web Interactiva

Para la presentación, he preparado un puente Web -> gRPC. Sigue estos pasos:

1. **Instalar dependencias:**
   ```bash
   go get github.com/joho/godotenv
   ```

2. **Ejecutar el Servidor gRPC:**
   ```bash
   go run server/main.go
   ```

3. **Ejecutar la Interfaz Web:**
   ```bash
   go run client/web_server.go
   ```

4. **Abrir en el navegador:**
   `http://localhost:8080`

## 4. Tips para la Exposición
- Muestra el archivo `.proto` primero para explicar el "contrato".
- Usa la interfaz web para hacer pruebas en vivo (Celsius a Fahrenheit, etc.).
- Explica cómo gRPC permite una comunicación eficiente y tipada entre servicios.
