from datetime import datetime
from sqlalchemy import Column, Integer, String, Float, DateTime
from .database import Base


class Inventario(Base):
    __tablename__ = "inventarios"

    id = Column(Integer, primary_key=True, index=True)
    producto = Column(String(100), nullable=False)
    cantidad = Column(Integer, nullable=False)
    sede = Column(String(50), nullable=False)
    actualizado_en = Column(DateTime, default=datetime.utcnow)


class Pedido(Base):
    __tablename__ = "pedidos"

    id = Column(Integer, primary_key=True, index=True)
    cliente = Column(String(100), nullable=False)
    producto = Column(String(100), nullable=False)
    cantidad = Column(Integer, nullable=False)
    estado = Column(String(50), default="CREADO")
    sede = Column(String(50), nullable=False)
    creado_en = Column(DateTime, default=datetime.utcnow)


class Temperatura(Base):
    __tablename__ = "temperaturas"

    id = Column(Integer, primary_key=True, index=True)
    almacen = Column(String(100), nullable=False)
    temperatura = Column(Float, nullable=False)
    sede = Column(String(50), nullable=False)
    alerta = Column(String(150), default="Temperatura normal")
    registrado_en = Column(DateTime, default=datetime.utcnow)


class Envio(Base):
    __tablename__ = "envios"

    id = Column(Integer, primary_key=True, index=True)
    pedido_id = Column(Integer, nullable=False)
    estado = Column(String(50), nullable=False)
    ubicacion = Column(String(100), nullable=False)
    sede = Column(String(50), nullable=False)
    actualizado_en = Column(DateTime, default=datetime.utcnow)


class Vehiculo(Base):
    __tablename__ = "vehiculos"

    id = Column(Integer, primary_key=True, index=True)
    placa = Column(String(20), nullable=False)
    latitud = Column(Float, nullable=False)
    longitud = Column(Float, nullable=False)
    sede = Column(String(50), nullable=False)
    actualizado_en = Column(DateTime, default=datetime.utcnow)