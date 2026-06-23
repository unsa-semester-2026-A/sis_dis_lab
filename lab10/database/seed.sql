DO $$
BEGIN
RAISE NOTICE 'Inicializando registros base en la base de datos: %', current_database();
END $$;

-- Carga inicial de inventarios
INSERT INTO inventarios (producto, cantidad, sede) VALUES
('Granada Roja', 140, 'Lima'),
('Kiwi Verde', 190, 'Lima'),
('Mandarina Satsuma', 210, 'Lima'),
('Pimiento Amarillo', 170, 'Lima'),
('Filete de Salmón', 95, 'Lima'),
('Pulpo Congelado', 75, 'Lima'),
('Espinaca Fresca', 260, 'Lima'),
('Alcachofa', 145, 'Lima'),
('Melón Cantaloupe', 110, 'Lima');

-- Pedidos iniciales
INSERT INTO pedidos (cliente, producto, cantidad, estado, sede) VALUES
('Fresh Market SAC', 'Granada Roja', 40, 'En proceso', 'Lima'),
('Gourmet Perú', 'Filete de Salmón', 20, 'Entregado', 'Lima'),
('Agroexport Perú', 'Kiwi Verde', 90, 'Pendiente', 'Lima'),
('Mercado Santa Anita', 'Mandarina Satsuma', 60, 'En proceso', 'Lima'),
('Distribuciones del Sur', 'Pulpo Congelado', 18, 'Entregado', 'Lima'),
('Hotel Costa del Sol', 'Espinaca Fresca', 50, 'Pendiente', 'Lima'),
('Hipermercados Vega', 'Melón Cantaloupe', 70, 'En proceso', 'Lima'),
('Restaurante El Pez Marino', 'Filete de Salmón', 30, 'Entregado', 'Lima'),
('Exportaciones Agrícolas SAC', 'Alcachofa', 100, 'Pendiente', 'Lima'),
('Vida Natural EIRL', 'Pimiento Amarillo', 85, 'En proceso', 'Lima');

-- Registros de temperatura
INSERT INTO temperaturas (almacen, temperatura, sede) VALUES
('Centro Logístico Callao', 4.2, 'Lima'),
('Depósito Frío Lurín', 5.0, 'Lima'),
('Planta de Conservación Ate', 3.8, 'Lima'),
('Almacén Refrigerado Villa El Salvador', 4.5, 'Lima');

-- Seguimiento de envíos
INSERT INTO envios (pedido_id, estado, ubicacion) VALUES
(1, 'En tránsito', 'Ruta Panamericana Sur - Km 32'),
(2, 'Entregado', 'San Borja - Sucursal Principal'),
(3, 'En preparación', 'Centro de Operaciones - Callao'),
(4, 'En tránsito', 'Av. Universitaria - Los Olivos'),
(5, 'Entregado', 'Magdalena del Mar - Oficina Comercial'),
(6, 'Pendiente', 'Depósito Central - Lima Norte'),
(7, 'En tránsito', 'Autopista Ramiro Prialé'),
(8, 'Entregado', 'Miraflores - Restaurante Asociado'),
(9, 'En preparación', 'Zona de Embalaje - Ate'),
(10, 'En tránsito', 'Av. Faucett - Callao');

-- Posición de vehículos
INSERT INTO vehiculos (placa, latitud, longitud, sede) VALUES
('FDX-201', -12.065421, -77.041233, 'Lima'),
('FDX-202', -12.071554, -77.028954, 'Lima'),
('FDX-203', -12.049875, -77.036542, 'Lima'),
('FDX-204', -12.083214, -77.021785, 'Lima'),
('FDX-205', -12.058745, -77.054112, 'Lima'),
('FDX-206', -12.091365, -77.046321, 'Lima');

DO $$
BEGIN
RAISE NOTICE 'Proceso de carga inicial completado correctamente';
END $$;
