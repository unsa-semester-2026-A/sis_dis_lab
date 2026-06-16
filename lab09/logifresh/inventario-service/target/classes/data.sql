INSERT INTO productos (id, codigo, nombre, stock, stock_minimo, fecha_creacion, version) VALUES
(1,  'PRD-001', 'Leche Entera 1L',        1000000, 50,   NOW(), 1),
(2,  'PRD-002', 'Yogurt Natural 500ml',   1000000, 40,   NOW(), 1),
(3,  'PRD-003', 'Queso Fresco 400g',      1000000, 25,   NOW(), 1),
(4,  'PRD-004', 'Mantequilla 250g',       1000000, 20,   NOW(), 1),
(5,  'PRD-005', 'Crema de Leche 200ml',   1000000, 20,   NOW(), 1),
(6,  'PRD-006', 'Pollo Entero 3kg',       1000000, 30,   NOW(), 1),
(7,  'PRD-007', 'Pechuga de Pollo 1kg',   1000000, 30,   NOW(), 1),
(8,  'PRD-008', 'Carne Molida 500g',      1000000, 25,   NOW(), 1),
(9,  'PRD-009', 'Lomo de Cerdo 1kg',      1000000, 15,   NOW(), 1),
(10, 'PRD-010', 'Salchichas 500g',        1000000, 40,   NOW(), 1),
(11, 'PRD-011', 'Jamon de Pavo 200g',     1000000, 30,   NOW(), 1),
(12, 'PRD-012', 'Huevos Docena',          1000000, 50,   NOW(), 1),
(13, 'PRD-013', 'Lechuga Hidroponica',    1000000, 30,   NOW(), 1),
(14, 'PRD-014', 'Tomate Saladet 1kg',     1000000, 35,   NOW(), 1),
(15, 'PRD-015', 'Brocoli 500g',           1000000, 20,   NOW(), 1),
(16, 'PRD-016', 'Zanahoria 1kg',          1000000, 40,   NOW(), 1),
(17, 'PRD-017', 'Papa Blanca 5kg',        1000000, 80,   NOW(), 1),
(18, 'PRD-018', 'Cebolla Cabezona 1kg',   1000000, 60,   NOW(), 1),
(19, 'PRD-019', 'Manzana Roja 1kg',       1000000, 45,   NOW(), 1),
(20, 'PRD-020', 'Naranja 1kg',            1000000, 55,   NOW(), 1)
ON CONFLICT (id) DO UPDATE SET
  stock = EXCLUDED.stock,
  stock_minimo = EXCLUDED.stock_minimo,
  version = EXCLUDED.version;

ALTER TABLE productos ALTER COLUMN id RESTART WITH 21;
