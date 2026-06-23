from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from sqlalchemy import text

from .database import engine, get_db
from . import models

from .routes import inventarios_router, pedidos_router, temperaturas_router, envios_router, vehiculos_router

models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="API FedEx Perú - Lab 10 Replicación",
    description="Backend base para registrar datos críticos de FedEx Perú usando FastAPI y PostgreSQL.",
    version="1.0.0"
)


@app.get("/")
def inicio():
    return {
        "mensaje": "API FedEx Perú funcionando correctamente",
        "base_datos": "PostgreSQL",
        "modulos": [
            "inventarios",
            "pedidos",
            "temperaturas",
            "envios",
            "vehiculos"
        ]
    }


@app.get("/health")
def health_check(db: Session = Depends(get_db)):
    try:
        # Verify db connection is active
        db.execute(text("SELECT 1"))
        return {
            "status": "OK",
            "servicio": "backend-fedex",
            "base_datos": "PostgreSQL"
        }
    except Exception as e:
        raise HTTPException(
            status_code=500,
            detail=f"Error de conexión con la base de datos: {str(e)}"
        )


app.include_router(inventarios_router)
app.include_router(pedidos_router)
app.include_router(temperaturas_router)
app.include_router(envios_router)
app.include_router(vehiculos_router)