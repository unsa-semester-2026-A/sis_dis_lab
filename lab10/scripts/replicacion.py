import psycopg2
import time
from datetime import datetime

def log(msg):
    print(f"{datetime.now().strftime('%H:%M:%S')} - {msg}")

print("=" * 70)
log(f"Iniciando sincronización de datos")
print("=" * 70)

CONFIGS = {
    'lima': {
        'host': 'localhost',
        'port': 5433,
        'database': 'fedex_lima',
        'user': 'fedex_user',
        'password': 'fedex_pass'
    },
    'bogota': {
        'host': 'localhost',
        'port': 5434,
        'database': 'fedex_bogota',
        'user': 'fedex_user',
        'password': 'fedex_pass'
    },
    'santiago': {
        'host': 'localhost',
        'port': 5435,
        'database': 'fedex_santiago',
        'user': 'fedex_user',
        'password': 'fedex_pass'
    },
    'mexico': {
        'host': 'localhost',
        'port': 5436,
        'database': 'fedex_mexico',
        'user': 'fedex_user',
        'password': 'fedex_pass'
    }
}

TABLAS = ['inventarios', 'pedidos', 'temperaturas', 'envios', 'vehiculos']

def conectar_db(config, nombre):
    """Conectar a una base de datos"""
    try:
        conn = psycopg2.connect(**config)
        log(f"Conectado a {nombre} (localhost:{config['port']})")
        return conn
    except Exception as e:
        log(f"ERROR conectando a {nombre}: {str(e)[:60]}")
        return None

def sincronizar_tabla(lima_conn, replica_conn, tabla, nombre_replica):
    """Sincronizar una tabla de Lima a una réplica"""
    try:
        # 1. Leer datos de Lima
        with lima_conn.cursor() as cur:
            cur.execute(f"SELECT * FROM {tabla}")
            rows = cur.fetchall()
            columnas = [desc[0] for desc in cur.description]
        
        if not rows:
            log(f"  Tabla {tabla} vacía en Lima")
            return True
        
        log(f"  Sincronizando {len(rows)} registros de {tabla} a {nombre_replica}")
        
        # 2. Insertar/Actualizar en réplica
        with replica_conn.cursor() as cur:
            # Construir consulta UPSERT
            columnas_str = ', '.join(columnas)
            placeholders = ', '.join(['%s'] * len(columnas))
            
            # Columnas para UPDATE (excluyendo id)
            update_cols = [col for col in columnas if col != 'id']
            update_str = ', '.join([f"{col}=EXCLUDED.{col}" for col in update_cols])
            
            upsert = f"""
                INSERT INTO {tabla} ({columnas_str})
                VALUES ({placeholders})
                ON CONFLICT (id) DO UPDATE SET {update_str}
            """
            
            # Ejecutar para cada fila
            for row in rows:
                cur.execute(upsert, row)
            
            replica_conn.commit()
            
            # Reset sequence to prevent duplicate key errors on insert in case replica becomes primary
            try:
                with replica_conn.cursor() as seq_cur:
                    seq_cur.execute(f"SELECT setval(pg_get_serial_sequence('{tabla}', 'id'), COALESCE(MAX(id), 0) + 1, false) FROM {tabla}")
                replica_conn.commit()
            except Exception as seq_err:
                log(f"  Advertencia: No se pudo resetear secuencia para {tabla}: {str(seq_err)[:50]}")
                replica_conn.rollback()
                
            log(f"  OK {len(rows)} registros sincronizados a {nombre_replica}")
            return True
            
    except Exception as e:
        log(f"  ERROR sincronizando {tabla} a {nombre_replica}: {str(e)[:80]}")
        replica_conn.rollback()
        return False

def verificar_datos():
    """Verificar que los datos existen en las bases de datos"""
    print("\n" + "-" * 70)
    log("Verificando datos en las bases de datos...")
    
    for nombre, config in CONFIGS.items():
        try:
            conn = psycopg2.connect(**config)
            with conn.cursor() as cur:
                cur.execute("SELECT COUNT(*) FROM inventarios")
                count = cur.fetchone()[0]
                cur.execute("SELECT COUNT(*) FROM pedidos")
                pedidos = cur.fetchone()[0]
            conn.close()
            print(f"  {nombre.capitalize()}: {count} inventarios, {pedidos} pedidos")
        except Exception as e:
            print(f"  {nombre.capitalize()}: ERROR - {str(e)[:40]}")

def main():
    print("\n" + "=" * 70)
    log("VERIFICANDO CONEXIONES")
    print("-" * 70)
    
    # 1. Conectar a Lima (fuente)
    lima_conn = conectar_db(CONFIGS['lima'], "Lima")
    if not lima_conn:
        log("ERROR: No se puede continuar sin Lima")
        log("Asegúrate que los contenedores estén corriendo: docker-compose up -d")
        return
    
    # 2. Conectar a réplicas
    replicas = []
    for nombre in ['bogota', 'santiago', 'mexico']:
        conn = conectar_db(CONFIGS[nombre], nombre.capitalize())
        if conn:
            replicas.append((nombre.capitalize(), conn))
    
    if not replicas:
        log("ERROR: No hay réplicas disponibles")
        lima_conn.close()
        return
    
    # 3. Verificar datos existentes
    verificar_datos()
    
    # 4. Iniciar sincronización
    print("\n" + "=" * 70)
    log("INICIANDO SINCRONIZACION")
    print("-" * 70)
    
    total = len(TABLAS) * len(replicas)
    exitos = 0
    
    for tabla in TABLAS:
        print(f"\n  Tabla: {tabla.upper()}")
        for nombre_replica, replica_conn in replicas:
            if sincronizar_tabla(lima_conn, replica_conn, tabla, nombre_replica):
                exitos += 1
    
    # 5. Cerrar conexiones
    lima_conn.close()
    for _, conn in replicas:
        conn.close()
    
    # 6. Resultado final
    print("\n" + "=" * 70)
    log(f"RESUMEN: {exitos}/{total} operaciones exitosas")
    
    if exitos == total:
        log("TODAS LAS TABLAS SINCRONIZADAS CORRECTAMENTE")
        print("=" * 70)
        print("\nVerificando datos finales...")
        verificar_datos()
    else:
        log(f"ADVERTENCIA: {total - exitos} operaciones fallaron")
        print("=" * 70)

if __name__ == "__main__":
    # Esperar un poco para asegurar que las DBs están listas
    time.sleep(2)
    main()