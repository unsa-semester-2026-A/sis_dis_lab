CREATE TABLE inventario(
    id serial PRIMARY KEY,
    producto VARCHAR(100),
    stock INTEGER
);

INSERT INTO inventario(producto, stock)
VALUES
('Paracetamol', 50),
('Ibuprofeno', 30),
('Amoxicilina', 20);
