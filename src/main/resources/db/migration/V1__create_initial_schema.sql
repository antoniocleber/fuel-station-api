-- ============================================================
-- V1__create_initial_schema.sql
-- Criação das tabelas iniciais do sistema de abastecimento
-- ============================================================

-- Tabela: tipos de combustível
CREATE TABLE fuel_type (
    id         BIGINT        NOT NULL AUTO_INCREMENT,
    name       VARCHAR(100)  NOT NULL,
    price_per_liter DECIMAL(10, 3) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_fuel_type PRIMARY KEY (id),
    CONSTRAINT uq_fuel_type_name UNIQUE (name),
    CONSTRAINT chk_fuel_type_price CHECK (price_per_liter > 0)
);

-- Tabela: bombas de combustível (relacionada a um tipo de combustível)
CREATE TABLE fuel_pump (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    name         VARCHAR(100) NOT NULL,
    fuel_type_id BIGINT       NOT NULL,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_fuel_pump PRIMARY KEY (id),
    CONSTRAINT uq_fuel_pump_name UNIQUE (name),
    CONSTRAINT fk_fuel_pump_fuel_type FOREIGN KEY (fuel_type_id)
        REFERENCES fuel_type (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Tabela: abastecimentos
CREATE TABLE fueling (
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    pump_id      BIGINT         NOT NULL,
    fueling_date DATE           NOT NULL,
    liters       DECIMAL(10, 3) NOT NULL,
    total_value  DECIMAL(10, 2) NOT NULL,
    created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_fueling PRIMARY KEY (id),
    CONSTRAINT fk_fueling_pump FOREIGN KEY (pump_id)
        REFERENCES fuel_pump (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_fueling_liters CHECK (liters > 0),
    CONSTRAINT chk_fueling_total_value CHECK (total_value > 0)
);

-- Índices para melhorar performance em consultas comuns
CREATE INDEX idx_fueling_pump_id ON fueling (pump_id);
CREATE INDEX idx_fueling_date    ON fueling (fueling_date);
CREATE INDEX idx_fuel_pump_fuel_type ON fuel_pump (fuel_type_id);
