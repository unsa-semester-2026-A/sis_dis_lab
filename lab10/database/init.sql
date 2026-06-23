-- Eliminar tablas si existen (orden inverso a dependencias)
DROP TABLE IF EXISTS envios;
DROP TABLE IF EXISTS temperaturas;
DROP TABLE IF EXISTS vehiculos;
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS inventarios;

-- Tabla de Inventarios
CREATE TABLE inventarios (
    id SERIAL PRIMARY KEY,
    producto VARCHAR(255) NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 0,
    sede VARCHAR(100) NOT NULL,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Pedidos
CREATE TABLE pedidos (
    id SERIAL PRIMARY KEY,
    cliente VARCHAR(255) NOT NULL,
    producto VARCHAR(255) NOT NULL,
    cantidad INTEGER NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'Pendiente',
    sede VARCHAR(100) NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Temperaturas
CREATE TABLE temperaturas (
    id SERIAL PRIMARY KEY,
    almacen VARCHAR(255) NOT NULL,
    temperatura DECIMAL(5,2) NOT NULL,
    sede VARCHAR(100) NOT NULL,
    alerta VARCHAR(150) NOT NULL DEFAULT 'Temperatura normal',
    registrado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Envíos
CREATE TABLE envios (
    id SERIAL PRIMARY KEY,
    pedido_id INTEGER REFERENCES pedidos(id) ON DELETE CASCADE,
    estado VARCHAR(50) NOT NULL DEFAULT 'En preparación',
    ubicacion VARCHAR(255) NOT NULL,
    sede VARCHAR(100) NOT NULL DEFAULT 'Lima',
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Vehículos
CREATE TABLE vehiculos (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(20) NOT NULL UNIQUE,
    latitud DECIMAL(10,8) NOT NULL,
    longitud DECIMAL(11,8) NOT NULL,
    sede VARCHAR(100) NOT NULL,
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para mejorar rendimiento en consultas
CREATE INDEX idx_inventarios_sede ON inventarios(sede);
CREATE INDEX idx_pedidos_sede ON pedidos(sede);
CREATE INDEX idx_pedidos_estado ON pedidos(estado);
CREATE INDEX idx_envios_estado ON envios(estado);
CREATE INDEX idx_vehiculos_sede ON vehiculos(sede);

-- Mensaje de confirmación
DO $$
BEGIN
    RAISE NOTICE '- Tablas creadas exitosamente %', current_database();
END $$;