-- Table: financas.lancamento

-- DROP TABLE IF EXISTS financas.lancamento;


CREATE TABLE IF NOT EXISTS financas.lancamento (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    descricao VARCHAR(150) NOT NULL,
    mes INTEGER NOT NULL,
    ano INTEGER NOT NULL,
    valor NUMERIC(16, 2) NOT NULL,
    tipo VARCHAR(20) CHECK (tipo in ('RECEITA', 'DESPESA')) NOT NULL,
    status VARCHAR(20) CHECK (status in ('EFETIVADO', 'CANCELADO', 'PENDENTE')) NOT NULL,
    id_usuario BIGINT REFERENCES financas.usuario(id),
    data_cadastro DATE DEFAULT NOW()
);

--ALTER TABLE IF EXISTS financas.lancamento
--OWNER to postgres;