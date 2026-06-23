# Usa una imagen oficial de Python ligera
FROM python:3.9-slim

# Establece el directorio de trabajo
WORKDIR /app

# Copia los requerimientos y los instala
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copia todo el código del backend
COPY . .

# Expone el puerto donde correrá FastAPI
EXPOSE 8000

# Comando para iniciar la aplicación
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]