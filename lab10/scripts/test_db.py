# scripts/test_db.py
import psycopg2

print("Probando conexiones a las bases de datos...")
print("-" * 40)

# Configuración usando puertos mapeados
dbs = [
    ("Lima", 5433),
    ("Bogotá", 5434),
    ("Santiago", 5435),
    ("México", 5436),
]

for nombre, puerto in dbs:
    try:
        conn = psycopg2.connect(
            host='localhost',
            port=puerto,
            database=f'fedex_{nombre.lower()}',
            user='fedex_user',
            password='fedex_pass'
        )
        with conn.cursor() as cur:
            cur.execute("SELECT COUNT(*) FROM inventarios")
            count = cur.fetchone()[0]
        conn.close()
        print(f"[OK] {nombre}: {count} registros en inventarios")
    except Exception as e:
        print(f"[ERROR] {nombre}: {str(e)[:50]}...")