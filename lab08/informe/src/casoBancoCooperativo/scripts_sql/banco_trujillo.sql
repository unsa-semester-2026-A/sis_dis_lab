CREATE TABLE cuentas(
    id serial PRIMARY KEY,
    titular VARCHAR(100),
    saldo NUMERIC(12, 2)
);

INSERT INTO cuentas(titular, saldo)
VALUES
('Mathias Barrios', 30000)
