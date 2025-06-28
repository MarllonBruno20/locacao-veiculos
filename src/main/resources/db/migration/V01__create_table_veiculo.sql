-- Criação da sequência para geração do ID
CREATE SEQUENCE veiculo_codigo_seq START WITH 1 INCREMENT BY 1;

-- Criação da tabela veiculo
CREATE TABLE veiculo (
    codigo BIGINT PRIMARY KEY DEFAULT nextval('veiculo_codigo_seq'),

    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    placa VARCHAR(10) NOT NULL UNIQUE,

    numero_portas INTEGER NOT NULL CHECK (numero_portas BETWEEN 2 AND 4),
    numero_passageiros INTEGER NOT NULL CHECK (numero_passageiros BETWEEN 5 AND 7),

    valor_diaria NUMERIC(10, 2) NOT NULL CHECK (valor_diaria > 0),

    status VARCHAR(50) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    combustivel VARCHAR(50) NOT NULL,
    tipo_cambio VARCHAR(50) NOT NULL,

     image_url TEXT NOT NULL
);