CREATE TABLE inventario(
    id serial PRIMARY KEY,
    producto VARCHAR(100),
    stock INTEGER
);

INSERT INTO inventario(producto, stock)
VALUES
('Paracetamol', 100),
('Ibuprofeno', 80),
('Amoxicilina', 50);
