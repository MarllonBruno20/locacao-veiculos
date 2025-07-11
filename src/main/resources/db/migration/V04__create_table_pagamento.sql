CREATE SEQUENCE pagamento_codigo_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE pagamento (
    codigo BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('pagamento_codigo_seq'),

    data_pagamento TIMESTAMP WITHOUT TIME ZONE,

    valor_pago NUMERIC(10, 2) NOT NULL,

    forma_pagamento VARCHAR(50) NOT NULL,
    status_pagamento VARCHAR(50) NOT NULL
);