-- Table: financas.usuario

-- DROP TABLE IF EXISTS financas.usuario;

CREATE TABLE IF NOT EXISTS financas.usuario (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    nome VARCHAR(150),
    email VARCHAR(30),
    senha VARCHAR(20),
    data_cadastro DATE DEFAULT NOW()
);

TABLESPACE pg_default;

--ALTER TABLE IF EXISTS financas.usuario
--OWNER to postgres;