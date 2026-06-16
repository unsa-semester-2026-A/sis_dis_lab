# Prueba de Carga - LogiFresh

Script k6 para evaluar el rendimiento del endpoint `POST /api/pedidos/procesar`.

## Requisitos

- [k6](https://k6.io/docs/getting-started/installation/) v0.50+ instalado
- Microservicios LogiFresh ejecutándose (`docker compose up -d`)

## Perfil de carga (rampa)

| Etapa | Duracion | VUs | Proposito |
|-------|----------|-----|-----------|
| 1 | 1 min | 0 → 20 | Calentamiento |
| 2 | 1 min | 20 → 50 | Carga moderada |
| 3 | 2 min | 50 → 100 | Carga maxima |
| 4 | 1 min | 100 → 50 | Descenso |
| 5 | 1 min | 50 → 0 | Enfriamiento |

Se usa rampa gradual para evitar saturar el servidor de golpe.

## Ejecucion

```bash
# Prueba completa: 6 minutos con rampa
k6 run k6-load-test.js

# Con URL personalizada
k6 run -e BASE_URL=http://192.168.1.100:8081 k6-load-test.js

# Exportar resultados a JSON
k6 run --out json=resultados.json k6-load-test.js
```

## Metricas monitoreadas

| Metrica | Descripcion |
|---------|-------------|
| `success_count` | Respuestas exitosas (200/201) |
| `eof_count` | Conexiones cerradas por el servidor (EOF) |
| `timeout_count` | Timeouts de conexion |
| `error_500_count` | Errores HTTP 500 |
| `otros_count` | Otros errores |
| `http_req_duration` | Latencia total de requests |
| `pedido_duration` | Latencia especifica del endpoint |
| `error_rate` | Tasa de errores de validacion |

## Umbrales (thresholds)

| Umbral | Limite |
|--------|--------|
| p95 de latencia total | < 15s |
| Tasa de fallos HTTP | < 30% |
| p95 de latencia de pedidos | < 15s |
| Tasa de errores de validacion | < 30% |
