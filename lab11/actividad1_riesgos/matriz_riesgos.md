# Actividad 1 — Identificación y Análisis Cuantitativo de Amenazas

En esta actividad se identifican los activos críticos de LogiMarket Perú, se mapean las amenazas y vulnerabilidades que causaron los incidentes reportados, y se evalúa cuantitativamente el riesgo para proponer controles de seguridad robustos.

---

## 1. Metodología de Evaluación Cuantitativa de Riesgos

Para ir más allá de una clasificación cualitativa simple, se utiliza la metodología de análisis cuantitativo basada en el producto de **Probabilidad (Frecuencia)** e **Impacto (Severidad)**.

### Escala de Probabilidad (P)
| Nivel | Valor | Descripción |
| :--- | :---: | :--- |
| **Raro** | 1 | Muy poco probable que ocurra en el año. |
| **Poco Probable** | 2 | Podría ocurrir de manera excepcional. |
| **Posible** | 3 | Ocurre ocasionalmente. |
| **Probable** | 4 | Es muy probable que ocurra (evidencia histórica o incidentes recurrentes). |
| **Casi Seguro** | 5 | Ocurre de forma inminente y repetitiva. |

### Escala de Impacto (I)
| Nivel | Valor | Descripción |
| :--- | :---: | :--- |
| **Insignificante** | 1 | Sin impacto financiero ni operativo. |
| **Menor** | 2 | Impacto leve, interrupción menor corregida en minutos. |
| **Moderado** | 3 | Interrupción de horas, daño menor a la reputación. |
| **Mayor** | 4 | Pérdida de datos confidenciales, interrupción de días, impacto financiero notable. |
| **Catastrófico** | 5 | Paralización total de la empresa, multas legales severas, fuga masiva de datos críticos. |

### Matriz de Cálculo de Riesgo ($R = P \times I$)
- **Riesgo Bajo (1 - 4):** Aceptar el riesgo o aplicar controles básicos.
- **Riesgo Medio (5 - 12):** Mitigar a mediano plazo, implementar monitoreo básico.
- **Riesgo Alto (13 - 20):** Mitigación urgente, controles de seguridad robustos requeridos.
- **Riesgo Crítico (21 - 25):** Mitigación inmediata, paralización de operaciones inseguras.

---

## 2. Matriz de Riesgos - LogiMarket Perú

| ID Activo | Activo Crítico | Amenaza Identificada | Vulnerabilidad Explotada | Prob (P) | Imp (I) | Riesgo (P×I) | Nivel de Riesgo | Incidente Asociado | Control Recomendado (Mejora) |
| :--- | :--- | :--- | :--- | :---: | :---: | :---: | :---: | :--- | :--- |
| **ACT-01** | **Portal Web** | Acceso no autorizado y robo de información de clientes. | Ausencia de autenticación multifactor (MFA) y control de sesiones concurrentes. | 4 | 4 | **16** | <span style="color:red">**ALTO**</span> | **Incidente 5:** Credenciales compartidas e inicios de sesión desde ubicaciones imposibles. | Implementar MFA obligatorio (TOTP), política de bloqueo (Lockout) tras 3 intentos, y revocación automática de sesiones concurrentes/IPs sospechosas. |
| **ACT-02** | **App Móvil** | Interceptación de datos en tránsito e inyección de payloads. | Falta de validación estricta del certificado del servidor (SSL Pinning) y almacenamiento local inseguro. | 3 | 4 | **12** | <span style="color:orange">**MEDIO**</span> | *General* | Forzar SSL/TLS Pinning en el cliente móvil y encriptar la caché local mediante Keychain/Keystore. |
| **ACT-03** | **API Gateway / Microservicios** | Bypass de seguridad de API y consumo directo de endpoints internos. | Microservicios que no validan la firma criptográfica de los tokens JWT de manera autónoma, confiando ciegamente en el Gateway. | 5 | 5 | **25** | <span style="color:darkred">**CRÍTICO**</span> | **Incidente 1:** Accesos no autorizados a servicios internos saltándose la validación del Gateway. | Establecer mTLS (TLS Mutuo) para la comunicación entre servicios y obligar a cada microservicio a verificar la firma del JWT usando llaves públicas (criptografía asimétrica). |
| **ACT-04** | **Base de Datos Inventario** | Modificación maliciosa del stock de productos sin dejar rastro de autoría. | Ausencia de auditoría de logs (Write-Once-Read-Many) y asignación de privilegios excesivos. | 4 | 5 | **20** | <span style="color:red">**ALTO**</span> | **Incidente 4:** Alteración maliciosa del stock sin posibilidad de realizar auditoría forense. | Implementar auditoría inmutable a nivel de Base de Datos y un pipeline centralizado de logs JSON. Aplicar control de acceso basado en roles (RBAC). |
| **ACT-05** | **Gateway de Pagos** | Interceptación de datos de tarjetas de crédito y transacciones (MitM). | Canal de comunicación expuesto en texto plano (HTTP) en lugar de HTTPS. | 3 | 5 | **15** | <span style="color:red">**ALTO**</span> | **Incidente 2:** Interceptación de credenciales y datos en la red debido al uso de protocolos no cifrados. | Forzar comunicación mediante TLS 1.3 con suites de cifrado seguras (como AES-256-GCM), inhabilitando HTTP en texto plano y TLS obsoletos (1.0/1.1). |
| **ACT-06** | **Microservicio Logística** | Indisponibilidad del servicio de despachos por agotamiento de recursos. | Ausencia de políticas de Rate Limiting y cuotas de uso por API/Cliente. | 4 | 4 | **16** | <span style="color:red">**ALTO**</span> | **Incidente 3:** Caída de la API de logística debido a una inundación masiva de peticiones (DoS). | Configurar Rate Limiting por dirección IP y por Token en el API Gateway, y limitar recursos de cómputo en contenedores Docker del microservicio. |

---

## 3. Conclusión del Análisis de Riesgos

La arquitectura actual de LogiMarket Perú presenta un nivel de riesgo promedio **alto (17.33/25)**, destacando el **Bypass de Microservicios (Riesgo 25)** como el punto de falla más urgente. Las medidas propuestas en las siguientes actividades abordarán sistemáticamente estos riesgos para reducirlos a un nivel **bajo (< 5)** aceptable para el negocio.
