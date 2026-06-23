from datetime import datetime
from pydantic import BaseModel, Field, ConfigDict


class InventarioCreate(BaseModel):
    producto: str = Field(..., min_length=2)
    cantidad: int = Field(..., gt=0)
    sede: str = Field(..., min_length=2)


class InventarioOut(InventarioCreate):
    id: int
    actualizado_en: datetime

    model_config = ConfigDict(from_attributes=True)


class PedidoCreate(BaseModel):
    cliente: str = Field(..., min_length=2)
    producto: str = Field(..., min_length=2)
    cantidad: int = Field(..., gt=0)
    sede: str = Field(..., min_length=2)


class PedidoOut(PedidoCreate):
    id: int
    estado: str
    creado_en: datetime

    model_config = ConfigDict(from_attributes=True)


class TemperaturaCreate(BaseModel):
    almacen: str = Field(..., min_length=2)
    temperatura: float
    sede: str = Field(..., min_length=2)


class TemperaturaOut(TemperaturaCreate):
    id: int
    alerta: str
    registrado_en: datetime

    model_config = ConfigDict(from_attributes=True)


class EnvioCreate(BaseModel):
    pedido_id: int = Field(..., gt=0)
    estado: str = Field(..., min_length=2)
    ubicacion: str = Field(..., min_length=2)
    sede: str = Field(..., min_length=2)


class EnvioOut(EnvioCreate):
    id: int
    actualizado_en: datetime

    model_config = ConfigDict(from_attributes=True)


class VehiculoCreate(BaseModel):
    placa: str = Field(..., min_length=3)
    latitud: float
    longitud: float
    sede: str = Field(..., min_length=2)


class VehiculoOut(VehiculoCreate):
    id: int
    actualizado_en: datetime

    model_config = ConfigDict(from_attributes=True)