# Actividad 5 — Sistema de Auditoría y Logs Centralizados

Este directorio contiene el diseño técnico detallado para la implementación de un sistema centralizado de logs y auditoría que proporcione trazabilidad e inmutabilidad en LogiMarket Perú.

## Contenido

- **`audit_flow.svg`**: Diagrama de flujo que modela el pipeline de recolección de logs, indexación centralizada, motor de alertas y políticas de retención diferenciadas.

---

## Resumen del Diseño Propuesto

### 1. Taxonomía de Logs Estructurados (JSON)
Para evitar los logs planos difíciles de indexar (como los que causaron el Incidente 4), el sistema define una taxonomía estructurada en formato JSON con campos clave: timestamp, service_name, severity (INFO, WARNING, CRITICAL), event_id, actor, action, status, ip_address y details.

### 2. Alertas SOC en Tiempo Real (Mitigación del Incidente 4)
Todo evento crítico (como el bypass directo o cambios manuales de stock en inventario) genera un log con nivel `CRITICAL`, disparando una alerta inmediata al centro de monitoreo (SOC) vía Slack/Correo, permitiendo una rápida intervención.

### 3. Políticas de Retención de Logs
Para optimizar recursos de almacenamiento y cumplir con normativas de seguridad:
- **Hot Tier (INFO):** Retención local por 30 días en base de datos veloz; depuración automática.
- **Warm Tier (WARNING):** Retención por 90 días en almacenamiento comprimido de costo medio.
- **Cold Tier (CRITICAL):** Archivado en frío de forma inmutable durante 365 días para auditoría legal.

## Subida a Git
Este módulo y sus archivos son responsabilidad de:
- **Integrante:** Quispe Condori, Alvaro Raul
