import psycopg2

def connect(dbname):
    return psycopg2.connect(
        dbname = dbname,
        user = "postgres",
        password = "",
        host = "localhost",
        port = "5432"
    )
