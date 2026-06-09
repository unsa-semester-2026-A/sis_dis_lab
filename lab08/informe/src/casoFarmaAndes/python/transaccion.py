# Módulo de transacciones distribuidas — FarmaAndes S.A.
# Implementa el protocolo Two-Phase Commit (2PC) sobre PostgreSQL

from db import connect

# Diccionario que mapea nombres de bases de datos a nombres legibles
SEDES = {
    "almacen_arequipa": "Arequipa",
    "almacen_lima": "Lima"
}

def obtener_inventario(nombre_db):
    """Obtiene el stock actual de todos los productos en una sede"""

    conn = connect(nombre_db)
    cur = conn.cursor()

    # SQL: consultar stock de todos los productos ordenados alfabéticamente
    cur.execute("""
        SELECT producto, stock
        FROM inventario
        ORDER BY producto
    """)

    datos = cur.fetchall()
    conn.close()
    return datos

def obtener_productos():
    """Obtiene la lista de productos disponibles (desde Arequipa como referencia)"""

    conn = connect("almacen_arequipa")
    cur = conn.cursor()

    # SQL: obtener solo los nombres de los productos
    cur.execute("""
        SELECT producto
        FROM inventario
        ORDER BY producto
    """)

    productos = [fila[0] for fila in cur.fetchall()]
    conn.close()
    return productos

# START-SNIPPET,ejecutar_2pc

def ejecutar_2pc(
    origen_db,
    destino_db,
    producto,
    cantidad,
    simular_fallo=False
):
    """
    Protocolo Two-Phase Commit (2PC)
    - Fase 1 (PREPARE): verifica stock, descuenta en origen, pregunta a destino
    - Fase 2 (COMMIT/ROLLBACK): confirma o revierte los cambios en ambos nodos
    """

    logs = []
    # Validación inicial: origen y destino no pueden ser la misma sede
    if origen_db == destino_db:
        logs.append("ERROR: origen y destino no pueden ser iguales")
        return logs

    # Conectar a ambas bases de datos (cada una es un nodo distribuido)
    origen = connect(origen_db)
    destino = connect(destino_db)

    try:
        # --- FASE 1: PREPARE ---
        # Desactivar autocommit para tener control manual sobre la transacción
        origen.autocommit = False
        destino.autocommit = False

        cur_o = origen.cursor()
        cur_d = destino.cursor()

        nombre_origen = SEDES[origen_db]
        nombre_destino = SEDES[destino_db]

        logs.append("FASE 1: PREPARE")
        logs.append(f"Transferencia de {cantidad} unidades")
        logs.append(f"Producto: {producto}")
        logs.append(f"Origen: {nombre_origen}")
        logs.append(f"Destino: {nombre_destino}")

        # SQL: verificar stock disponible en el nodo origen
        cur_o.execute(
            """
            SELECT stock
            FROM inventario
            WHERE producto = %s
            """,
            (producto,)
        )

        fila = cur_o.fetchone()
        if fila is None:
            raise Exception("Producto no encontrado")

        stock_actual = fila[0]
        logs.append(f"Stock disponible en origen: {stock_actual}")

        # Validar que haya stock suficiente para la transferencia
        if stock_actual < cantidad:
            raise Exception("Stock insuficiente")

        # Ambos nodos responden OK en la fase PREPARE
        logs.append(f"Nodo {nombre_origen} responde OK")
        logs.append(f"Nodo {nombre_destino} responde OK")

        # SQL: descontar stock del almacén origen (UPDATE en nodo origen)
        cur_o.execute(
            """
            UPDATE inventario
            SET stock = stock - %s
            WHERE producto = %s
            """,
            (cantidad, producto)
        )

        logs.append(f"Stock descontado en {nombre_origen}")

        # START-SNIPPET,fallo_check
        # SIMULACIÓN DE FALLO (Ejercicio 2):
        # Si está activa, se lanza una excepción antes de actualizar el destino
        if simular_fallo:
            raise Exception(f"El nodo {nombre_destino} dejó de responder")
        # END-SNIPPET

        # SQL: incrementar stock en el almacén destino (UPDATE en nodo destino)
        cur_d.execute(
            """
            UPDATE inventario
            SET stock = stock + %s
            WHERE producto = %s
            """,
            (cantidad, producto)
        )

        logs.append(f"Stock incrementado en {nombre_destino}")
        logs.append("Todos los nodos aceptaron la transacción")

        # --- FASE 2: COMMIT ---
        logs.append("FASE 2: COMMIT")

        # SQL: confirmar cambios en ambos nodos (COMMIT en PostgreSQL)
        origen.commit()
        destino.commit()

        logs.append("COMMIT GLOBAL EJECUTADO")
        logs.append("Transferencia completada correctamente")

    # START-SNIPPET,fallo_rollback
    except Exception as e:
        # Si algo falla, se ejecuta ROLLBACK en ambos nodos
        # SQL: deshacer cambios en ambas bases de datos
        origen.rollback()
        destino.rollback()

        logs.append(f"ERROR: {e}")
        logs.append("ROLLBACK GLOBAL EJECUTADO")
        logs.append("La transacción fue cancelada")
    # END-SNIPPET

    finally:
        # Cerrar conexiones a las bases de datos
        origen.close()
        destino.close()

    return logs

# END-SNIPPET
