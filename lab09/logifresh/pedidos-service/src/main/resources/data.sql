INSERT INTO pedidos (id, cliente, producto_id, cantidad, precio_unitario, descuento, subtotal, total, estado, fecha_creacion) VALUES
(1,  'Supermercado Central',  1,  30,  100.00, 0,  3000.00, 3000.00,  'CONFIRMADO', NOW()),
(2,  'Mayorista SA',          3,  50,  80.00,  5,  4000.00, 3800.00,  'CONFIRMADO', NOW()),
(3,  'Distribuidora Norte',   5,  100, 60.00,  10, 6000.00, 5400.00,  'CONFIRMADO', NOW()),
(4,  'Comercial del Sur',     8,  25,  120.00, 0,  3000.00, 3000.00,  'CONFIRMADO', NOW()),
(5,  'Almacenes Este',        12, 75,  40.00,  5,  3000.00, 2850.00,  'CONFIRMADO', NOW()),
(6,  'Mercado Oeste',         15, 10,  150.00, 0,  1500.00, 1500.00,  'CANCELADO',  NOW()),
(7,  'Frigorificos del Valle', 2,  200, 25.00,  10, 5000.00, 4500.00,  'CONFIRMADO', NOW()),
(8,  'Restaurante La Costa',  10, 40,  55.00,  0,  2200.00, 2200.00,  'CONFIRMADO', NOW()),
(9,  'Hotel Central',         18, 60,  35.00,  5,  2100.00, 1995.00,  'CONFIRMADO', NOW()),
(10, 'Catering Express',      20, 15,  90.00,  0,  1350.00, 1350.00,  'CONFIRMADO', NOW())
ON CONFLICT (id) DO NOTHING;

ALTER TABLE pedidos ALTER COLUMN id RESTART WITH 11;
