# Proxy CORS para consumir el servicio SOAP de DneOnline desde navegador.
# Expone un endpoint REST local y llama a SOAP con zeep.

import json
from http.server import BaseHTTPRequestHandler, HTTPServer
from socketserver import ThreadingMixIn

try:
    from zeep import Client
except ImportError:
    raise SystemExit("Error: Instale zeep con: pip install zeep")

WSDL_URL = "http://www.dneonline.com/calculator.asmx?WSDL"
HOST = "localhost"
PORT = 8090

SOAP_CLIENT = Client(WSDL_URL)


class ThreadingHTTPServer(ThreadingMixIn, HTTPServer):
    daemon_threads = True


def ejecutar_operacion(operacion, a, b):
    if operacion == "Add":
        return SOAP_CLIENT.service.Add(a, b), "SOAP: Add"
    if operacion == "Subtract":
        return SOAP_CLIENT.service.Subtract(a, b), "SOAP: Subtract"
    if operacion == "Multiply":
        return SOAP_CLIENT.service.Multiply(a, b), "SOAP: Multiply"
    if operacion == "Divide":
        if b == 0:
            raise ValueError("Division por cero no permitida.")
        return SOAP_CLIENT.service.Divide(a, b), "SOAP: Divide"
    if operacion == "Modulo":
        if b == 0:
            raise ValueError("Modulo por cero no permitido.")
        cociente = SOAP_CLIENT.service.Divide(a, b)
        producto = SOAP_CLIENT.service.Multiply(b, cociente)
        residuo = SOAP_CLIENT.service.Subtract(a, producto)
        return residuo, "SOAP: Divide + Multiply + Subtract"
    raise ValueError("Operacion no soportada.")


class SoapProxyHandler(BaseHTTPRequestHandler):
    def _send_headers(self, status=200, content_type="application/json"):
        self.send_response(status)
        self.send_header("Content-Type", content_type)
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "Content-Type")
        self.send_header("Access-Control-Max-Age", "86400")
        self.end_headers()

    def do_OPTIONS(self):
        self._send_headers(204)

    def do_POST(self):
        if self.path != "/api/calculadora":
            self._send_headers(404)
            self.wfile.write(json.dumps({"error": "Ruta no encontrada"}).encode("utf-8"))
            return

        try:
            length = int(self.headers.get("Content-Length", "0"))
            body = self.rfile.read(length).decode("utf-8")
            payload = json.loads(body)
            operacion = payload.get("operacion")
            a = int(payload.get("a"))
            b = int(payload.get("b"))
        except Exception as exc:
            self._send_headers(400)
            self.wfile.write(
                json.dumps({"error": f"Solicitud invalida: {exc}"}).encode("utf-8")
            )
            return

        try:
            resultado, detalle = ejecutar_operacion(operacion, a, b)
            self._send_headers(200)
            self.wfile.write(
                json.dumps(
                    {
                        "ok": True,
                        "operacion": operacion,
                        "a": a,
                        "b": b,
                        "resultado": resultado,
                        "wsdl": WSDL_URL,
                        "detalle": detalle,
                    }
                ).encode("utf-8")
            )
        except Exception as exc:
            self._send_headers(500)
            self.wfile.write(json.dumps({"error": str(exc)}).encode("utf-8"))

    def log_message(self, format, *args):
        return


def main():
    server = ThreadingHTTPServer((HOST, PORT), SoapProxyHandler)
    print(f"Proxy CORS activo en http://{HOST}:{PORT}/api/calculadora")
    print(f"Usando WSDL remoto: {WSDL_URL}")
    server.serve_forever()


if __name__ == "__main__":
    main()
