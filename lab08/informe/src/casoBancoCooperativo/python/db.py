# Módulo de conexión a PostgreSQL — Sistema Nacional de Bancos Cooperativos
# Cada "nodo" es una base de datos PostgreSQL independiente

import psycopg2

def connect(dbname):
    """Conecta a una base de datos PostgreSQL local (cada sede es un nodo)"""
    return psycopg2.connect(
        dbname=dbname,
        user="sdhm",
        password="",
        host="localhost",
        port="5432"
    )
