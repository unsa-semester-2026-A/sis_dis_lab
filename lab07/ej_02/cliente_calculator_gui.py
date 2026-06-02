# Cliente SOAP en Python con Interfaz Grafica (Tkinter) para la Calculadora de DneOnline.
import sys
import threading

try:
    import tkinter as tk
    from tkinter import messagebox
    from tkinter import ttk
except ImportError:
    print("Error: Se requiere la libreria tkinter para ejecutar la GUI.")
    sys.exit(1)

try:
    from zeep import Client
except ImportError:
    print("Error: Se requiere la libreria zeep para ejecutar este script.")
    print("Instalela usando: pip install zeep")
    sys.exit(1)

class CalculadoraSOAPGUI:
    def __init__(self, root):
        self.root = root
        self.root.title("Calculadora SOAP Interactiva (Python)")
        self.root.geometry("520x420")
        self.root.resizable(False, False)
        
        # Paleta de colores premium (Dark mode)
        self.bg_color = "#0f172a"      # slate-900
        self.card_color = "#1e293b"    # slate-800
        self.accent_blue = "#3b82f6"   # blue-500
        self.accent_green = "#10b981"  # emerald-500
        self.text_main = "#f8fafc"     # slate-50
        self.text_muted = "#94a3b8"    # slate-400
        
        self.root.configure(bg=self.bg_color)
        
        # Establecer estilos
        self.style = ttk.Style()
        self.style.theme_use("clam")
        
        # Cliente Zeep (se inicializara en un hilo secundario)
        self.client = None
        self.wsdl_url = 'http://www.dneonline.com/calculator.asmx?WSDL'
        
        self.crear_widgets()
        
        # Conectar al servicio SOAP en segundo plano
        threading.Thread(target=self.conectar_soap, daemon=True).start()

    def crear_widgets(self):
        # Titulo Principal
        lbl_titulo = tk.Label(
            self.root, 
            text="Calculadora SOAP (DneOnline)", 
            font=("Helvetica", 18, "bold"), 
            bg=self.bg_color, 
            fg=self.accent_blue
        )
        lbl_titulo.pack(pady=15)
        
        # Panel del Formulario (Card)
        form_frame = tk.Frame(self.root, bg=self.card_color, bd=1, relief="flat", padx=20, pady=20)
        form_frame.pack(fill="both", expand=True, padx=20, pady=10)
        
        # Operando A
        lbl_a = tk.Label(form_frame, text="Operando A:", font=("Helvetica", 11), bg=self.card_color, fg=self.text_main)
        lbl_a.grid(row=0, column=0, sticky="w", pady=5)
        
        self.entry_a = tk.Entry(
            form_frame, 
            font=("Helvetica", 12), 
            bg="#0f172a", 
            fg=self.text_main, 
            insertbackground="white", 
            bd=1, 
            relief="solid"
        )
        self.entry_a.grid(row=0, column=1, columnspan=2, sticky="ew", padx=(10, 0), pady=5)
        self.entry_a.insert(0, "15")
        
        # Operando B
        lbl_b = tk.Label(form_frame, text="Operando B:", font=("Helvetica", 11), bg=self.card_color, fg=self.text_main)
        lbl_b.grid(row=1, column=0, sticky="w", pady=5)
        
        self.entry_b = tk.Entry(
            form_frame, 
            font=("Helvetica", 12), 
            bg="#0f172a", 
            fg=self.text_main, 
            insertbackground="white", 
            bd=1, 
            relief="solid"
        )
        self.entry_b.grid(row=1, column=1, columnspan=2, sticky="ew", padx=(10, 0), pady=5)
        self.entry_b.insert(0, "5")
        
        # Botones de Operaciones
        btn_frame = tk.Frame(form_frame, bg=self.card_color)
        btn_frame.grid(row=2, column=0, columnspan=3, pady=15)
        
        self.btn_sumar = tk.Button(
            btn_frame, text="Sumar (+)", font=("Helvetica", 10, "bold"), 
            bg=self.accent_blue, fg="white", activebackground="#2563eb", activeforeground="white",
            relief="flat", width=10, command=lambda: self.ejecutar_operacion("Add")
        )
        self.btn_sumar.grid(row=0, column=0, padx=5, pady=5)
        
        self.btn_restar = tk.Button(
            btn_frame, text="Restar (-)", font=("Helvetica", 10, "bold"), 
            bg=self.accent_blue, fg="white", activebackground="#2563eb", activeforeground="white",
            relief="flat", width=10, command=lambda: self.ejecutar_operacion("Subtract")
        )
        self.btn_restar.grid(row=0, column=1, padx=5, pady=5)
        
        self.btn_multiplicar = tk.Button(
            btn_frame, text="Multiplicar (*)", font=("Helvetica", 10, "bold"), 
            bg=self.accent_blue, fg="white", activebackground="#2563eb", activeforeground="white",
            relief="flat", width=11, command=lambda: self.ejecutar_operacion("Multiply")
        )
        self.btn_multiplicar.grid(row=0, column=2, padx=5, pady=5)
        
        self.btn_dividir = tk.Button(
            btn_frame, text="Dividir (/)", font=("Helvetica", 10, "bold"), 
            bg=self.accent_blue, fg="white", activebackground="#2563eb", activeforeground="white",
            relief="flat", width=10, command=lambda: self.ejecutar_operacion("Divide")
        )
        self.btn_dividir.grid(row=0, column=3, padx=5, pady=5)
        
        # Mostrar Resultado
        self.lbl_resultado = tk.Label(
            form_frame, 
            text="Resultado: Esperando operacion...", 
            font=("Helvetica", 13, "bold"), 
            bg=self.card_color, 
            fg=self.accent_green
        )
        self.lbl_resultado.grid(row=3, column=0, columnspan=3, pady=10)
        
        # Barra de Estado
        self.lbl_estado = tk.Label(
            self.root, 
            text="Estableciendo conexion SOAP...", 
            font=("Helvetica", 9, "italic"), 
            bg=self.bg_color, 
            fg=self.text_muted,
            anchor="w"
        )
        self.lbl_estado.pack(fill="x", side="bottom", padx=20, pady=5)

    def conectar_soap(self):
        try:
            self.client = Client(self.wsdl_url)
            self.lbl_estado.config(
                text="Conexion activa con " + self.wsdl_url, 
                fg=self.accent_green
            )
        except Exception as e:
            self.lbl_estado.config(
                text="Error al conectar con el servicio SOAP.", 
                fg="red"
            )
            messagebox.showerror(
                "Error de conexion", 
                "No se pudo descargar el WSDL del servicio:\n" + str(e)
            )

    def ejecutar_operacion(self, operacion_name):
        if not self.client:
            messagebox.showwarning(
                "Espera", 
                "Aun se esta estableciendo conexion con el servicio SOAP."
            )
            return
            
        try:
            val_a = int(self.entry_a.get().strip())
            val_b = int(self.entry_b.get().strip())
        except ValueError:
            messagebox.showerror(
                "Error de formato", 
                "Los operandos deben ser numeros enteros validos."
            )
            return

        # Deshabilitar botones temporalmente
        self.set_botones_state(tk.DISABLED)
        self.lbl_resultado.config(text="Llamando a SOAP (" + operacion_name + ")...", fg=self.accent_blue)
        
        # Ejecutar peticion en un hilo para no congelar la UI
        threading.Thread(
            target=self.llamar_soap_remoto, 
            args=(operacion_name, val_a, val_b), 
            daemon=True
        ).start()

    def llamar_soap_remoto(self, op, a, b):
        try:
            result = None
            if op == "Add":
                result = self.client.service.Add(a, b)
                simbolo = "+"
            elif op == "Subtract":
                result = self.client.service.Subtract(a, b)
                simbolo = "-"
            elif op == "Multiply":
                result = self.client.service.Multiply(a, b)
                simbolo = "*"
            elif op == "Divide":
                if b == 0:
                    raise Exception("Division por cero no permitida.")
                result = self.client.service.Divide(a, b)
                simbolo = "/"
                
            msg = "Resultado: " + str(a) + " " + simbolo + " " + str(b) + " = " + str(result)
            self.root.after(0, self.actualizar_resultado_exito, msg)
        except Exception as e:
            self.root.after(0, self.actualizar_resultado_error, str(e))

    def actualizar_resultado_exito(self, msg):
        self.lbl_resultado.config(text=msg, fg=self.accent_green)
        self.set_botones_state(tk.NORMAL)

    def actualizar_resultado_error(self, err_msg):
        self.lbl_resultado.config(text="Error al procesar la operacion.", fg="red")
        self.set_botones_state(tk.NORMAL)
        messagebox.showerror("Error SOAP", "La peticion remota fallo:\n" + err_msg)

    def set_botones_state(self, state):
        self.btn_sumar.config(state=state)
        self.btn_restar.config(state=state)
        self.btn_multiplicar.config(state=state)
        self.btn_dividir.config(state=state)

if __name__ == "__main__":
    root = tk.Tk()
    app = CalculadoraSOAPGUI(root)
    root.mainloop()
