CREATE SEQUENCE pessoa_codigo_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE pessoa (
    codigo BIGINT PRIMARY KEY DEFAULT nextval('pessoa_codigo_seq'),
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE, -- Considere adicionar uma máscara ou validação de formato no backend
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    email VARCHAR(255)
);