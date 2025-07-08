CREATE SEQUENCE locacao_codigo_seq
    START WITH 1
    INCREMENT BY 1;


CREATE TABLE locacao (
    codigo BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('locacao_codigo_seq'),

    data_locacao_inicio TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    data_locacao_fim TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    valor_total NUMERIC(10, 2) NOT NULL,

    status_locacao VARCHAR(50) NOT NULL,
    local_retirada VARCHAR(50) NOT NULL,
    local_devolucao VARCHAR(50) NOT NULL,

    codigo_veiculo BIGINT NOT NULL,

    codigo_usuario BIGINT NOT NULL,

    codigo_pagamento BIGINT UNIQUE,

    CONSTRAINT fk_locacao_to_veiculo   FOREIGN KEY (codigo_veiculo)   REFERENCES veiculo(codigo),
    CONSTRAINT fk_locacao_to_usuario   FOREIGN KEY (codigo_usuario)   REFERENCES usuario(codigo),
    CONSTRAINT fk_locacao_to_pagamento FOREIGN KEY (codigo_pagamento) REFERENCES pagamento(codigo)
);