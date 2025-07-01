CREATE SEQUENCE usuario_codigo_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE usuario (
    codigo BIGINT PRIMARY KEY DEFAULT nextval('usuario_codigo_seq'),
    nome_usuario VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- CLIENTE ou ADMINISTRADOR
    codigo_pessoa BIGINT NOT NULL,
    FOREIGN KEY (codigo_pessoa) REFERENCES pessoa(codigo)
);