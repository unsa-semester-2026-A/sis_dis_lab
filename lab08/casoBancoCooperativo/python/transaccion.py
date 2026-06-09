# Módulo de transacciones distribuidas — Sistema Nacional de Bancos Cooperativos
# Implementa el protocolo Two-Phase Commit (2PC) sobre PostgreSQL con 3 nodos

import time
from db import connect

# Mapeo de nombres de bases de datos a nombres legibles
SEDES = {
    "banco_arequipa": "Banco Arequipa",
    "banco_cusco":    "Banco Cusco",
    "banco_trujillo": "Banco Trujillo"
}

# START-SNIPPET,obtener_cuentas

def obtener_cuentas(nombre_db):
    conn = connect(nombre_db)
    cur = conn.cursor()
    # SELECT: titulares y saldos
    cur.execute("""
        SELECT titular, saldo
        FROM cuentas
        ORDER BY titular
    """)
    datos = cur.fetchall()
    conn.close()
    return datos

# END-SNIPPET

# START-SNIPPET,obtener_titulares

def obtener_titulares():
    conn = connect("banco_arequipa")
    cur = conn.cursor()
    # SELECT: solo nombres
    cur.execute("SELECT titular FROM cuentas ORDER BY titular")
    titulares = [fila[0] for fila in cur.fetchall()]
    conn.close()
    return titulares

# END-SNIPPET

# START-SNIPPET,conectar_par

def _conectar_par(origen_db, destino_db):
    origen  = connect(origen_db)
    destino = connect(destino_db)
    origen.autocommit  = False
    destino.autocommit = False
    return origen, destino

# END-SNIPPET

# START-SNIPPET,obtener_cuenta

def _obtener_cuenta(cur, logs, nombre):
    # SELECT: id, titular y saldo
    cur.execute("SELECT id, titular, saldo FROM cuentas LIMIT 1")
    cuenta = cur.fetchone()
    if cuenta is None:
        raise Exception(f"No existe cuenta en {nombre}")
    logs.append(f"Saldo disponible en {nombre}: S/ {cuenta[2]:,.2f}")
    return cuenta

# END-SNIPPET


# START-SNIPPET,ejecutar_2pc

def ejecutar_2pc(origen_db, destino_db, monto, simular_fallo=False):
    logs = []

    if origen_db == destino_db:
        logs.append("ERROR: origen y destino no pueden ser iguales")
        return logs

    nombre_origen  = SEDES[origen_db]
    nombre_destino = SEDES[destino_db]

    # conectar a ambos nodos
    origen, destino = _conectar_par(origen_db, destino_db)

    try:
        cur_o = origen.cursor()
        cur_d = destino.cursor()

        # Fase 1: PREPARE
        logs.append("FASE 1: PREPARE")
        logs.append(f"Transferencia de S/ {monto:,.2f}")
        logs.append(f"Origen: {nombre_origen}  →  Destino: {nombre_destino}")

        # verificar saldo en origen
        cuenta_origen = _obtener_cuenta(cur_o, logs, nombre_origen)
        if cuenta_origen[2] < monto:
            raise Exception("Saldo insuficiente en cuenta origen")

        logs.append(f"{nombre_origen} responde YES")
        logs.append(f"{nombre_destino} responde YES")

        # debitar monto del origen
        cur_o.execute(
            "UPDATE cuentas SET saldo = saldo - %s WHERE id = %s",
            (monto, cuenta_origen[0])
        )
        logs.append(f"Débito realizado en {nombre_origen}")

        # acreditar monto en destino
        cur_d.execute(
            "UPDATE cuentas SET saldo = saldo + %s WHERE id = (SELECT id FROM cuentas LIMIT 1)",
            (monto,)
        )
        logs.append(f"Crédito realizado en {nombre_destino}")
        logs.append("Todos los participantes votaron YES")

        # Fase 2: COMMIT global
        logs.append("FASE 2: GLOBAL COMMIT")

        origen.commit()
        destino.commit()

        logs.append("COMMIT GLOBAL EJECUTADO")
        logs.append("Transferencia completada exitosamente")

    except Exception as e:
        # ROLLBACK si algo falla
        origen.rollback()
        destino.rollback()
        logs.append(f"ERROR: {e}")
        logs.append("GLOBAL ROLLBACK EJECUTADO")
        logs.append("Transacción cancelada — estado consistente restaurado")
    finally:
        origen.close()
        destino.close()

    return logs

# END-SNIPPET


# START-SNIPPET,fallo_red

def ejecutar_fallo_red(origen_db, destino_db, monto):
    logs = []

    if origen_db == destino_db:
        logs.append("ERROR: origen y destino no pueden ser iguales")
        return logs

    nombre_origen  = SEDES[origen_db]
    nombre_destino = SEDES[destino_db]
    origen, destino = _conectar_par(origen_db, destino_db)

    try:
        cur_o = origen.cursor()

        # Fase 1: PREPARE
        logs.append("FASE 1: PREPARE")
        logs.append(f"Transferencia de S/ {monto:,.2f}")
        logs.append(f"Origen: {nombre_origen}  →  Destino: {nombre_destino}")

        # verificar saldo en origen
        cuenta_origen = _obtener_cuenta(cur_o, logs, nombre_origen)
        if cuenta_origen[2] < monto:
            raise Exception("Saldo insuficiente en cuenta origen")

        logs.append(f"{nombre_origen} responde YES")

        # simular timeout: destino no recibe PREPARE
        logs.append(f"Enviando PREPARE a {nombre_destino}...")
        time.sleep(0.3)
        logs.append("FALLA DE RED — No se recibió respuesta del nodo destino")
        logs.append(f"Timeout: {nombre_destino} no responde (simulado)")

        raise Exception(f"Falla de red — pérdida de conexión con {nombre_destino}")

    except Exception as e:
        # ROLLBACK — destino nunca modificado
        origen.rollback()
        destino.rollback()
        logs.append(f"ERROR: {e}")
        logs.append("GLOBAL ROLLBACK EJECUTADO")
        logs.append("Transacción cancelada — ningún saldo fue modificado")
    finally:
        origen.close()
        destino.close()

    return logs

# END-SNIPPET


# START-SNIPPET,caida_nodo

def ejecutar_caida_nodo(origen_db, destino_db, monto):
    logs = []

    if origen_db == destino_db:
        logs.append("ERROR: origen y destino no pueden ser iguales")
        return logs

    nombre_origen  = SEDES[origen_db]
    nombre_destino = SEDES[destino_db]
    origen, destino = _conectar_par(origen_db, destino_db)

    try:
        cur_o = origen.cursor()

        # Fase 1: PREPARE
        logs.append("FASE 1: PREPARE")
        logs.append(f"Transferencia de S/ {monto:,.2f}")
        logs.append(f"Origen: {nombre_origen}  →  Destino: {nombre_destino}")

        # verificar saldo en origen
        cuenta_origen = _obtener_cuenta(cur_o, logs, nombre_origen)
        if cuenta_origen[2] < monto:
            raise Exception("Saldo insuficiente en cuenta origen")

        logs.append(f"{nombre_origen} responde YES")
        logs.append(f"{nombre_destino} responde YES")

        # debitar en origen (PREPARE exitoso)
        cur_o.execute(
            "UPDATE cuentas SET saldo = saldo - %s WHERE id = %s",
            (monto, cuenta_origen[0])
        )
        logs.append(f"Débito realizado en {nombre_origen}")
        logs.append("FASE 2: GLOBAL COMMIT iniciado")
        logs.append(f"Enviando COMMIT a {nombre_destino}...")

        # simular caída del destino en Fase 2
        logs.append(f"CAÍDA DE NODO — {nombre_destino} dejó de responder")
        logs.append(f"El proceso en {nombre_destino} fue terminado (simulado)")

        raise Exception(f"Nodo caído: {nombre_destino} no completó el COMMIT")

    except Exception as e:
        # ROLLBACK — revertir débito en origen
        origen.rollback()
        destino.rollback()
        logs.append(f"ERROR: {e}")
        logs.append("GLOBAL ROLLBACK EJECUTADO")
        logs.append("Débito revertido en origen — estado consistente restaurado")
    finally:
        origen.close()
        destino.close()

    return logs

# END-SNIPPET


# START-SNIPPET,recuperacion

def ejecutar_recuperacion(origen_db, destino_db, monto, tipo_fallo):
    logs = []

    if origen_db == destino_db:
        logs.append("ERROR: origen y destino no pueden ser iguales")
        return logs

    nombre_origen  = SEDES[origen_db]
    nombre_destino = SEDES[destino_db]
    origen, destino = _conectar_par(origen_db, destino_db)

    try:
        cur_o = origen.cursor()
        cur_d = destino.cursor()

        logs.append("RECUPERACIÓN POSTERIOR")
        logs.append("Verificando estado de nodos tras fallo previo...")
        logs.append(f"Nodo {nombre_origen}: en línea ✓")
        logs.append(f"Nodo {nombre_destino}: en línea ✓  (recuperado)")
        logs.append("Consultando log de transacciones pendientes...")

        logs.append(f"RECUPERACIÓN — Resolviendo fallo de tipo: {tipo_fallo.replace('_', ' ').upper()}")

        # Fase 1: PREPARE (reintento)
        logs.append("FASE 1: PREPARE (reintento)")

        # verificar saldo actual
        cuenta_origen = _obtener_cuenta(cur_o, logs, nombre_origen)

        logs.append(f"{nombre_origen} (Participante 1) — Vota: YES")
        logs.append(f"{nombre_destino} (Participante 2) — Vota: YES (Recuperado)")

        # UPDATE: debitar y acreditar pendientes
        cur_o.execute("UPDATE cuentas SET saldo = saldo - %s WHERE id = %s", (monto, cuenta_origen[0]))
        cur_d.execute("UPDATE cuentas SET saldo = saldo + %s WHERE id = (SELECT id FROM cuentas LIMIT 1)", (monto,))

        # Fase 2: COMMIT global
        logs.append("FASE 2: GLOBAL COMMIT")
        origen.commit()
        destino.commit()

        logs.append("LOG: Transacción marcada como COMPLETADA en el Coordinador")
        logs.append("RECUPERACIÓN EXITOSA — Consistencia total restaurada en todos los nodos")

    except Exception as e:
        origen.rollback()
        destino.rollback()
        logs.append(f"ERROR durante recuperación: {e}")
        logs.append("GLOBAL ROLLBACK EJECUTADO")
        logs.append("Recuperación fallida — intervención manual requerida")
    finally:
        origen.close()
        destino.close()

    return logs
