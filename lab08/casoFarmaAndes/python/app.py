# Aplicación web Flask — FarmaAndes S.A.
# Interfaz para ejecutar transferencias distribuidas con 2PC

from flask import Flask, render_template, request
from transaccion import (
    SEDES,
    obtener_inventario,
    obtener_productos,
    ejecutar_2pc
)

app = Flask(__name__, template_folder="../templates")

def renderizar_inicio(logs=None):
    """Renderiza la página principal con inventarios y logs de la última operación"""

    if logs is None:
        logs = []

    return render_template(
        "index.html",
        productos=obtener_productos(),
        inventario_arequipa=obtener_inventario("almacen_arequipa"),
        inventario_lima=obtener_inventario("almacen_lima"),
        sedes=SEDES,
        logs=logs
    )

def leer_formulario():
    """Extrae los datos del formulario HTML enviado por el usuario"""

    return {
        "producto": request.form["producto"],
        "cantidad": int(request.form["cantidad"]),
        "origen": request.form["origen"],
        "destino": request.form["destino"]
    }

# Ruta principal: muestra el estado inicial de los inventarios
@app.route("/")
def index():
    return renderizar_inicio()

# Ruta para transferencia exitosa (Ejercicio 1):
# Ejecuta 2PC con simular_fallo=False (comportamiento normal)
@app.route("/transferir", methods=["POST"])
def transferir():

    datos = leer_formulario()
    logs = ejecutar_2pc(
        datos["origen"],
        datos["destino"],
        datos["producto"],
        datos["cantidad"]
    )
    return renderizar_inicio(logs)

# Ruta para simulación de fallo (Ejercicio 2):
# Ejecuta 2PC con simular_fallo=True (el nodo destino "cae")
@app.route("/fallo", methods=["POST"])
def fallo():

    datos = leer_formulario()
    logs = ejecutar_2pc(
        datos["origen"],
        datos["destino"],
        datos["producto"],
        datos["cantidad"],
        simular_fallo=True
    )
    return renderizar_inicio(logs)

if __name__ == "__main__":
    app.run(debug=True)
