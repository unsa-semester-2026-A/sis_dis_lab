#!/bin/bash
# ============================================================
# fallo-lima.sh - Simulación de caída del nodo principal (Lima)
# ============================================================
# Este script simula una falla catastrófica en el nodo principal
# de PostgreSQL en Lima, deteniendo el contenedor Docker por
# un periodo de 20 minutos antes de restaurarlo automáticamente.
# ============================================================

CONTAINER="fedex_db_lima"
DOWNTIME_MINUTES=20
DOWNTIME_SECONDS=$((DOWNTIME_MINUTES * 60))

echo "============================================================"
echo "  SIMULACIÓN DE FALLO - NODO PRINCIPAL (LIMA)"
echo "============================================================"
echo ""
echo "  Contenedor:  $CONTAINER"
echo "  Tiempo de caída: $DOWNTIME_MINUTES minutos ($DOWNTIME_SECONDS segundos)"
echo "  Hora de inicio:  $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# 1. Verificar que el contenedor existe y está corriendo
if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"; then
    echo "[ERROR] El contenedor '$CONTAINER' no está corriendo."
    echo "        Asegúrate de ejecutar 'docker-compose up -d' primero."
    exit 1
fi

# 2. Detener el contenedor (simular fallo)
echo "------------------------------------------------------------"
echo "[$(date '+%H:%M:%S')] Deteniendo contenedor $CONTAINER..."
docker stop "$CONTAINER"

if [ $? -eq 0 ]; then
    echo "[$(date '+%H:%M:%S')] NODO LIMA CAÍDO - Contenedor detenido exitosamente."
else
    echo "[ERROR] No se pudo detener el contenedor."
    exit 1
fi

# 3. Mostrar estado de los contenedores restantes
echo ""
echo "------------------------------------------------------------"
echo "  ESTADO DE LOS NODOS DESPUÉS DEL FALLO:"
echo "------------------------------------------------------------"
docker ps --filter "name=fedex" --format "  {{.Names}}\t{{.Status}}"
echo ""

# 4. Cuenta regresiva de los 20 minutos
echo "------------------------------------------------------------"
echo "  Esperando $DOWNTIME_MINUTES minutos para simular la caída..."
echo "  Hora estimada de restauración: $(date -d "+${DOWNTIME_MINUTES} minutes" '+%H:%M:%S' 2>/dev/null || date -v+${DOWNTIME_MINUTES}M '+%H:%M:%S' 2>/dev/null || echo "en $DOWNTIME_MINUTES minutos")"
echo "  (Presiona Ctrl+C para cancelar la espera)"
echo "------------------------------------------------------------"
echo ""

ELAPSED=0
while [ $ELAPSED -lt $DOWNTIME_SECONDS ]; do
    REMAINING=$((DOWNTIME_SECONDS - ELAPSED))
    MINS=$((REMAINING / 60))
    SECS=$((REMAINING % 60))
    printf "\r  ⏳ Tiempo restante: %02d:%02d  " $MINS $SECS
    sleep 10
    ELAPSED=$((ELAPSED + 10))
done

echo ""
echo ""

# 5. Restaurar automáticamente
echo "------------------------------------------------------------"
echo "[$(date '+%H:%M:%S')] Tiempo de caída completado. Restaurando nodo Lima..."
docker start "$CONTAINER"

if [ $? -eq 0 ]; then
    echo "[$(date '+%H:%M:%S')] NODO LIMA RESTAURADO exitosamente."
else
    echo "[ERROR] No se pudo restaurar el contenedor. Ejecuta manualmente:"
    echo "        docker start $CONTAINER"
    exit 1
fi

# 6. Esperar a que PostgreSQL esté listo
echo ""
echo "  Esperando a que PostgreSQL esté listo..."
for i in $(seq 1 30); do
    if docker exec "$CONTAINER" pg_isready -U fedex_user -d fedex_lima > /dev/null 2>&1; then
        echo "[$(date '+%H:%M:%S')] PostgreSQL en Lima está listo y aceptando conexiones."
        break
    fi
    sleep 2
done

# 7. Estado final
echo ""
echo "============================================================"
echo "  ESTADO FINAL DE TODOS LOS NODOS:"
echo "============================================================"
docker ps --filter "name=fedex" --format "  {{.Names}}\t{{.Status}}"
echo ""
echo "  Simulación de fallo completada."
echo "  Hora de fin: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"
