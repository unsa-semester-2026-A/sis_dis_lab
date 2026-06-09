# Aplicación web Flask — Sistema Nacional de Bancos Cooperativos
# Interfaz para transferencias distribuidas con 2PC y simulación de fallos

from flask import Flask, render_template, request, session
from transaccion import (
    SEDES,
    obtener_cuentas,
    ejecutar_2pc,
    ejecutar_fallo_red,
    ejecutar_caida_nodo,
    ejecutar_recuperacion
)

app = Flask(__name__, template_folder="../templates")
app.secret_key = "bancos_cooperativos_2pc"


def renderizar_inicio(logs=None):
    if logs is None:
        logs = []
    return render_template(
        "index.html",
        banco_arequipa=obtener_cuentas("banco_arequipa"),
        banco_cusco=obtener_cuentas("banco_cusco"),
        banco_trujillo=obtener_cuentas("banco_trujillo"),
        sedes=SEDES,
        logs=logs,
        ultimo_fallo=session.get("ultimo_fallo")
    )


# Muestra los saldos iniciales de los 3 bancos (SELECT en cada nodo)
@app.route("/")
def index():
    return renderizar_inicio()


# Ejecuta 2PC: SELECT saldo → UPDATE débito → UPDATE crédito → COMMIT/ROLLBACK
@app.route("/transferir", methods=["POST"])
def transferir():
    origen = request.form["origen"]
    destino = request.form["destino"]
    monto = float(request.form["monto"])
    logs = ejecutar_2pc(origen, destino, monto)
    session.pop("ultimo_fallo", None)
    return renderizar_inicio(logs)


# Falla de red: SELECT saldo → timeout simulado → ROLLBACK
@app.route("/fallo_red", methods=["POST"])
def fallo_red():
    origen = request.form["origen"]
    destino = request.form["destino"]
    monto = float(request.form["monto"])
    logs = ejecutar_fallo_red(origen, destino, monto)
    session["ultimo_fallo"] = {
        "tipo": "fallo_red",
        "origen": origen,
        "destino": destino,
        "monto": monto
    }
    return renderizar_inicio(logs)


# Caída de nodo: SELECT → UPDATE débito → nodo cae → ROLLBACK
@app.route("/caida_nodo", methods=["POST"])
def caida_nodo():
    origen = request.form["origen"]
    destino = request.form["destino"]
    monto = float(request.form["monto"])
    logs = ejecutar_caida_nodo(origen, destino, monto)
    session["ultimo_fallo"] = {
        "tipo": "caida_nodo",
        "origen": origen,
        "destino": destino,
        "monto": monto
    }
    return renderizar_inicio(logs)


# Recuperación: SELECT → UPDATE débito/crédito → COMMIT (reintento completo)
@app.route("/recuperacion", methods=["POST"])
def recuperacion():
    fallo = session.get("ultimo_fallo")

    if not fallo:
        logs = [
            "RECUPERACION - Sin fallo previo registrado.",
            "No hay ningun nodo caido ni falla de red activa.",
            "Ejecuta primero Fallo de Red o Caida de Nodo para simular un fallo."
        ]
        return renderizar_inicio(logs)

    logs = ejecutar_recuperacion(
        fallo["origen"],
        fallo["destino"],
        fallo["monto"],
        fallo["tipo"]
    )
    session.pop("ultimo_fallo", None)
    return renderizar_inicio(logs)


if __name__ == "__main__":
    app.run(debug=True)
