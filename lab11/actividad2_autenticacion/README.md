# Actividad 2 — Diseño de Autenticación Segura (MFA + Contraseñas)

Este directorio contiene el diseño técnico detallado para la autenticación robusta y la protección de accesos del sistema distribuido de LogiMarket Perú.

## Contenido

- **`auth_flow.svg`**: Diagrama de arquitectura del flujo de autenticación, incluyendo la validación MFA (TOTP), y la revocación activa de sesiones concurrentes al detectar IPs sospechosas o conflictos geográficos.

---

## Resumen del Diseño Propuesto

### 1. Gestión de Contraseñas (Hashing con Scrypt)
Para proteger las credenciales en reposo, se utiliza el algoritmo **scrypt** en lugar de MD5 o SHA-256 planos. Es un algoritmo de hashing lento configurable que requiere gran cantidad de memoria y tiempo de CPU, haciéndolo altamente resistente a ataques por fuerza bruta basados en hardware dedicado (FPGAs o ASICs).

### 2. Autenticación Multifactor (MFA - TOTP)
El sistema implementa el algoritmo **TOTP (Time-Based One-Time Password)** según la norma **RFC 6238**. Se asocia una clave secreta única al usuario que genera códigos dinámicos de 6 dígitos temporales cada 30 segundos, validándose en el servidor con una ventana de tolerancia horaria.

### 3. Política de Bloqueo de Cuentas (Lockout)
Tras 3 intentos fallidos consecutivos de contraseña, la cuenta se bloquea temporalmente por 30 segundos como defensa ante ataques de fuerza bruta, rechazando de inmediato consultas costosas a la base de datos.

### 4. Revocación de Sesiones Concurrentes (Mitigación del Incidente 5)
Al iniciar sesión, se registran los metadatos en un almacén en memoria distribuido. Si se detecta concurrencia sospechosa o IPs conflictivas, se aplica **Session Blacklisting** automático, invalidando las sesiones anteriores para evitar credenciales compartidas o secuestradas.

## Subida a Git
Este módulo y sus archivos son responsabilidad de:
- **Integrante:** Huacani Jara Denise Andrea
