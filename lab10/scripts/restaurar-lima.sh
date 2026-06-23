#!/bin/bash
# ============================================================
# restaurar-lima.sh - Restauración del nodo principal (Lima)
# ============================================================
# Este script restaura el nodo principal de PostgreSQL en Lima
# después de una caída simulada, verificando la conectividad
# y el estado de los datos.
# ============================================================

CONTAINER="fedex_db_lima"

echo "============================================================"
echo "  RESTAURACIÓN DEL NODO PRINCIPAL (LIMA)"
echo "============================================================"
echo ""
echo "  Contenedor: $CONTAINER"
echo "  Hora de inicio: $(date '+%Y-%m-%d %H:%M:%S')"
echo ""

# 1. Verificar el estado actual del contenedor
echo "------------------------------------------------------------"
echo "  Verificando estado actual del contenedor..."
echo "------------------------------------------------------------"

CONTAINER_STATUS=$(docker inspect -f '{{.State.Status}}' "$CONTAINER" 2>/dev/null)

if [ $? -ne 0 ]; then
    echo "[ERROR] El contenedor '$CONTAINER' no existe."
    echo "        Ejecuta 'docker-compose up -d' para crear los contenedores."
    exit 1
fi

if [ "$CONTAINER_STATUS" = "running" ]; then
    echo "[INFO] El contenedor '$CONTAINER' ya está corriendo."
    echo "       No es necesario restaurarlo."
else
    echo "[INFO] Estado actual del contenedor: $CONTAINER_STATUS"
    echo ""

    # 2. Iniciar el contenedor
    echo "------------------------------------------------------------"
    echo "[$(date '+%H:%M:%S')] Iniciando contenedor $CONTAINER..."
    docker start "$CONTAINER"

    if [ $? -eq 0 ]; then
        echo "[$(date '+%H:%M:%S')] Contenedor iniciado exitosamente."
    else
        echo "[ERROR] No se pudo iniciar el contenedor."
        exit 1
    fi
fi

# 3. Esperar a que PostgreSQL esté completamente listo
echo ""
echo "------------------------------------------------------------"
echo "  Esperando a que PostgreSQL esté listo..."
echo "------------------------------------------------------------"

READY=false
for i in $(seq 1 30); do
    if docker exec "$CONTAINER" pg_isready -U fedex_user -d fedex_lima > /dev/null 2>&1; then
        echo "[$(date '+%H:%M:%S')] PostgreSQL está listo y aceptando conexiones."
        READY=true
        break
    fi
    printf "\r  Intento %d/30 - Esperando..." $i
    sleep 2
done

echo ""

if [ "$READY" = false ]; then
    echo "[ADVERTENCIA] PostgreSQL no respondió después de 60 segundos."
    echo "              Verifica los logs: docker logs $CONTAINER"
    exit 1
fi

# 4. Verificar integridad de los datos
echo ""
echo "------------------------------------------------------------"
echo "  Verificando integridad de los datos en Lima..."
echo "------------------------------------------------------------"

TABLES=("inventarios" "pedidos" "temperaturas" "envios" "vehiculos")

for TABLE in "${TABLES[@]}"; do
    COUNT=$(docker exec "$CONTAINER" psql -U fedex_user -d fedex_lima -t -c "SELECT COUNT(*) FROM $TABLE;" 2>/dev/null | tr -d ' ')
    if [ $? -eq 0 ]; then
        printf "  %-20s %s registros\n" "$TABLE:" "$COUNT"
    else
        printf "  %-20s ERROR al consultar\n" "$TABLE:"
    fi
done

# 5. Verificar conectividad del backend con Lima
echo ""
echo "------------------------------------------------------------"
echo "  Verificando conectividad del backend..."
echo "------------------------------------------------------------"

BACKEND_STATUS=$(docker ps --filter "name=fedex_backend_api" --format "{{.Status}}" 2>/dev/null)

if [ -n "$BACKEND_STATUS" ]; then
    echo "  Backend API: $BACKEND_STATUS"
    
    # Intentar hacer una petición al health endpoint
    HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8000/health 2>/dev/null)
    if [ "$HEALTH" = "200" ]; then
        echo "  Health check: OK (HTTP 200)"
    else
        echo "  Health check: Sin respuesta (el backend puede necesitar reinicio)"
        echo "  Ejecuta: docker restart fedex_backend_api"
    fi
else
    echo "  Backend API: No está corriendo"
fi

# 6. Estado final de todos los nodos
echo ""
echo "============================================================"
echo "  ESTADO FINAL DE TODOS LOS NODOS:"
echo "============================================================"
docker ps --filter "name=fedex" --format "  {{.Names}}\t{{.Status}}"
echo ""
echo "  Nodo Lima restaurado correctamente."
echo "  Hora de fin: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"
