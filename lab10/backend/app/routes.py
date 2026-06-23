from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from . import models, schemas
from .database import get_db


# Inventarios
inventarios_router = APIRouter(prefix="/inventarios", tags=["Inventarios"])


@inventarios_router.post("/", status_code=201)
def crear_inventario(data: schemas.InventarioCreate, db: Session = Depends(get_db)):
    nuevo = models.Inventario(**data.model_dump())
    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Inventario registrado correctamente",
        "data": schemas.InventarioOut.model_validate(nuevo)
    }


@inventarios_router.get("/")
def listar_inventarios(db: Session = Depends(get_db)):
    inventarios = db.query(models.Inventario).all()

    return {
        "total": len(inventarios),
        "data": [schemas.InventarioOut.model_validate(item) for item in inventarios]
    }


# Pedidos
pedidos_router = APIRouter(prefix="/pedidos", tags=["Pedidos"])


@pedidos_router.post("/", status_code=201)
def crear_pedido(data: schemas.PedidoCreate, db: Session = Depends(get_db)):
    nuevo = models.Pedido(**data.model_dump())

    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Pedido registrado correctamente",
        "data": schemas.PedidoOut.model_validate(nuevo)
    }


@pedidos_router.get("/")
def listar_pedidos(db: Session = Depends(get_db)):
    pedidos = db.query(models.Pedido).all()

    return {
        "total": len(pedidos),
        "data": [
            schemas.PedidoOut.model_validate(item)
            for item in pedidos
        ]
    }


# Temperaturas
temperaturas_router = APIRouter(prefix="/temperaturas", tags=["Temperaturas"])


@temperaturas_router.post("/", status_code=201)
def registrar_temperatura(data: schemas.TemperaturaCreate, db: Session = Depends(get_db)):
    alerta = "Temperatura normal"

    if data.temperatura > 8:
        alerta = "ALERTA: temperatura mayor a 8°C"
    elif data.temperatura < 0:
        alerta = "ALERTA: temperatura menor a 0°C"

    nueva = models.Temperatura(
        almacen=data.almacen,
        temperatura=data.temperatura,
        sede=data.sede,
        alerta=alerta
    )

    db.add(nueva)
    db.commit()
    db.refresh(nueva)

    return {
        "mensaje": "Temperatura registrada correctamente",
        "data": schemas.TemperaturaOut.model_validate(nueva)
    }


@temperaturas_router.get("/")
def listar_temperaturas(db: Session = Depends(get_db)):
    temperaturas = db.query(models.Temperatura).all()

    return {
        "total": len(temperaturas),
        "data": [schemas.TemperaturaOut.model_validate(item) for item in temperaturas]
    }


# Envios
envios_router = APIRouter(prefix="/envios", tags=["Envios"])

ESTADOS_VALIDOS = ["CREADO", "EN_TRANSITO", "ENTREGADO", "CANCELADO"]


@envios_router.post("/", status_code=201)
def registrar_envio(data: schemas.EnvioCreate, db: Session = Depends(get_db)):
    estado = data.estado.upper()

    if estado not in ESTADOS_VALIDOS:
        raise HTTPException(
            status_code=400,
            detail=f"Estado inválido. Estados permitidos: {ESTADOS_VALIDOS}"
        )

    nuevo = models.Envio(
        pedido_id=data.pedido_id,
        estado=estado,
        ubicacion=data.ubicacion,
        sede=data.sede
    )

    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Estado de envío registrado correctamente",
        "data": schemas.EnvioOut.model_validate(nuevo)
    }


@envios_router.get("/")
def listar_envios(db: Session = Depends(get_db)):
    envios = db.query(models.Envio).all()

    return {
        "total": len(envios),
        "data": [schemas.EnvioOut.model_validate(item) for item in envios]
    }


# Vehiculos
vehiculos_router = APIRouter(prefix="/vehiculos", tags=["Vehiculos"])


@vehiculos_router.post("/", status_code=201)
def registrar_vehiculo(data: schemas.VehiculoCreate, db: Session = Depends(get_db)):
    nuevo = models.Vehiculo(**data.model_dump())
    db.add(nuevo)
    db.commit()
    db.refresh(nuevo)

    return {
        "mensaje": "Ubicación de vehículo registrada correctamente",
        "data": schemas.VehiculoOut.model_validate(nuevo)
    }


@vehiculos_router.get("/")
def listar_vehiculos(db: Session = Depends(get_db)):
    vehiculos = db.query(models.Vehiculo).all()

    return {
        "total": len(vehiculos),
        "data": [schemas.VehiculoOut.model_validate(item) for item in vehiculos]
    }
