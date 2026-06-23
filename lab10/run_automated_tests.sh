#!/bin/bash
# ==============================================================================
# run_automated_tests.sh - Automatización de pruebas del Lab 10
# ==============================================================================
set -e

# Asegurar que el directorio de trabajo es el correcto
cd "$(dirname "$0")"

echo "======================================================================"
echo "           INICIANDO AUTOMATIZACIÓN DE PRUEBAS - LAB 10"
echo "======================================================================"

# 1. Preparar contenedores
echo ""
echo ">>> 1. Preparando estado de contenedores..."
docker stop fedex_backend_api_bogota 2>/dev/null || true
docker rm fedex_backend_api_bogota 2>/dev/null || true
docker start fedex_db_lima 2>/dev/null || true
docker start fedex_db_bogota 2>/dev/null || true
docker start fedex_db_santiago 2>/dev/null || true
docker start fedex_db_mexico 2>/dev/null || true
docker start fedex_backend_api 2>/dev/null || true

echo "Esperando que el nodo principal Lima esté listo..."
until docker exec fedex_db_lima pg_isready -U fedex_user -d fedex_lima > /dev/null 2>&1; do
  sleep 1
done

# 2. Re-inicializar bases de datos (init.sql)
echo ""
echo ">>> 2. Re-inicializando esquemas de base de datos..."
docker exec -i fedex_db_lima psql -U fedex_user -d fedex_lima < database/init.sql > /dev/null
docker exec -i fedex_db_bogota psql -U fedex_user -d fedex_bogota < database/init.sql > /dev/null
docker exec -i fedex_db_santiago psql -U fedex_user -d fedex_santiago < database/init.sql > /dev/null
docker exec -i fedex_db_mexico psql -U fedex_user -d fedex_mexico < database/init.sql > /dev/null
echo "[OK] Esquemas inicializados en todos los nodos."

# 3. Cargar datos semilla en Lima
echo ""
echo ">>> 3. Cargando datos semilla en Lima..."
docker exec -i fedex_db_lima psql -U fedex_user -d fedex_lima < database/seed.sql > /dev/null
echo "[OK] Semilla cargada."

# 4. Sincronizar bases de datos inicialmente
echo ""
echo ">>> 4. Sincronizando semilla de Lima a réplicas- Primera replicación"
./venv/bin/python scripts/replicacion.py > /dev/null
echo "[OK] Sincronización inicial exitosa."

# Copiar la colección a un archivo temporal para preservar las variables de Postman en Newman
cp Test_Replicacion.postman_collection.json temp_collection.json

# 5. Ejecutar secciones 1, 2 y 3 (Verificación, Registro, Consulta Pre-Replicación)
echo ""
echo "======================================================================"
echo ">>> 5. EJECUTANDO: Secciones 1, 2 y 3 (Operación normal en Lima)"
echo "======================================================================"
npx newman run temp_collection.json \
  --folder "1. Verificacion del del backend y la base de datos principal" \
  --folder "2. Registro de Datos en  el nodo Primary" \
  --folder "3. Consultar Datos en Lima (Pre-Replicación)" \
  --export-collection temp_collection.json

# 6. Sincronizar los nuevos registros antes del fallo
echo ""
echo ">>> 6. Replicando nuevos registros de Lima a réplicas..."
./venv/bin/python scripts/replicacion.py > /dev/null
echo "[OK] Replicación completada."

# 7. Simular Caída de Lima
echo ""
echo "======================================================================"
echo ">>> 7. SIMULANDO CAÍDA DE LIMA..."
echo "======================================================================"
docker stop fedex_db_lima > /dev/null
echo "[OK] Nodo postgres-lima detenido."

# 8. Ejecutar sección 4 (Comprobar fallo de lecturas/escrituras en Lima caída)
echo ""
echo ">>> 8. EJECUTANDO: Sección 4 (Verificación de fallo esperado)"
npx newman run temp_collection.json \
  --folder "4. Simular Caída de Lima" \
  --export-collection temp_collection.json

# 9. Realizar Failover a Bogotá (Reconfigurar backend)
echo ""
echo "======================================================================"
echo ">>> 9. EJECUTANDO FAILOVER A BOGOTÁ..."
echo "======================================================================"
docker stop fedex_backend_api > /dev/null
docker run -d --name fedex_backend_api_bogota \
  --network lab10_fedex_network \
  -p 8000:8000 \
  -e DATABASE_URL=postgresql+psycopg2://fedex_user:fedex_pass@postgres-bogota:5432/fedex_bogota \
  lab10_backend:latest > /dev/null

echo "Esperando que el backend reconfigurado responda en Bogotá..."
until curl -s http://localhost:8000/health > /dev/null; do
  sleep 1
done
echo "[OK] Backend conectado a Bogotá."

# 10. Ejecutar sección 5 (Comprobar operaciones en Bogotá)
echo ""
echo ">>> 10. EJECUTANDO: Sección 5 (Operaciones en Failover - Bogotá)"
npx newman run temp_collection.json \
  --folder "5. Failover a Bogotá (Réplica Prioritaria)" \
  --export-collection temp_collection.json

# 11. Recuperar Lima (Reiniciar nodo Lima)
echo ""
echo "======================================================================"
echo ">>> 11. RECUPERANDO NODO LIMA..."
echo "======================================================================"
docker start fedex_db_lima > /dev/null
echo "Esperando que Lima esté lista..."
until docker exec fedex_db_lima pg_isready -U fedex_user -d fedex_lima > /dev/null 2>&1; do
  sleep 1
done
echo "[OK] Nodo postgres-lima recuperado."

# 12. Revertir Failover (Reconectar backend a Lima)
echo ""
echo ">>> 12. Reconectando Backend a Lima..."
docker stop fedex_backend_api_bogota > /dev/null || true
docker rm fedex_backend_api_bogota > /dev/null || true
docker start fedex_backend_api > /dev/null

echo "Esperando que el backend reconectado a Lima responda..."
until curl -s http://localhost:8000/health > /dev/null; do
  sleep 1
done
echo "[OK] Backend reconectado a Lima."

# 13. Ejecutar sección 6 (Verificar estado final post-recuperación)
echo ""
echo "======================================================================"
echo ">>> 13. EJECUTANDO: Sección 6 (Recuperación y Estado Final)"
echo "======================================================================"
npx newman run temp_collection.json \
  --folder "6. Recuperación de Lima" \
  --export-collection temp_collection.json

# 14. Limpieza final
rm temp_collection.json
echo ""
echo "======================================================================"
echo "          FIN"
echo "======================================================================"
