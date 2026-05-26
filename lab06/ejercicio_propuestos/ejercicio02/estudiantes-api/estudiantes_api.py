from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

estudiantes = [
    {"nombre": "Ana",  "apellido": "Torres", "carrera": "Ing. Sistemas",   "codigo": "20230001"},
    {"nombre": "Luis", "apellido": "Ramos",  "carrera": "Ing. Industrial",  "codigo": "20230002"}
]

# GET /estudiantes — Consultar todos
@app.route('/estudiantes', methods=['GET'])
def listar():
    return jsonify(estudiantes)

# POST /estudiantes — Registrar nuevo estudiante
@app.route('/estudiantes', methods=['POST'])
def agregar():
    data = request.json
    if not data:
        return jsonify({"error": "Datos inválidos"}), 400
    estudiantes.append(data)
    return jsonify({"ok": True, "mensaje": "Estudiante registrado"}), 201

# PUT /estudiantes/<i> — Actualizar estudiante por índice
@app.route('/estudiantes/<int:i>', methods=['PUT'])
def actualizar(i):
    if i < 0 or i >= len(estudiantes):
        return jsonify({"error": "Índice fuera de rango"}), 404
    estudiantes[i] = request.json
    return jsonify({"actualizado": True})

# DELETE /estudiantes/<i> — Eliminar estudiante por índice
@app.route('/estudiantes/<int:i>', methods=['DELETE'])
def eliminar(i):
    if i < 0 or i >= len(estudiantes):
        return jsonify({"error": "Índice fuera de rango"}), 404
    eliminado = estudiantes.pop(i)
    return jsonify({"eliminado": True, "nombre": eliminado.get("nombre", "")})

if __name__ == '__main__':
    app.run(debug=True)