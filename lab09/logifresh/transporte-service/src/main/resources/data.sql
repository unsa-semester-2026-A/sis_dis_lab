INSERT INTO transportes (id, pedido_id, conductor, vehiculo, estado, fecha_asignacion) VALUES
(1,  1, 'Carlos Mendoza',  'CAM-001', 'ENTREGADO',  NOW()),
(2,  2, 'Maria Hernandez', 'CAM-002', 'EN_RUTA',    NOW()),
(3,  3, 'Juan Torres',     'CAM-003', 'ASIGNADO',   NOW()),
(4,  4, 'Ana Guerrero',    'CAM-004', 'ENTREGADO',  NOW()),
(5,  5, 'Pedro Ramirez',   'CAM-005', 'EN_RUTA',    NOW()),
(6,  7, 'Luis Vargas',     'CAM-006', 'ASIGNADO',   NOW()),
(7,  8, 'Sofia Castillo',  'CAM-007', 'ASIGNADO',   NOW()),
(8,  9, 'Diego Morales',   'CAM-008', 'ASIGNADO',   NOW()),
(9,  10, 'Claudia Rios',   'CAM-009', 'ASIGNADO',   NOW()),
(10, 6,  'Roberto Soto',   'CAM-010', 'ENTREGADO',  NOW())
ON CONFLICT (id) DO NOTHING;

ALTER TABLE transportes ALTER COLUMN id RESTART WITH 11;
