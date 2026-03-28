-- ============================================================
-- V3__add_manytomany_fuel_pump_fuel_type.sql
-- Refatorar relacionamento FuelPump-FuelType de ManyToOne para ManyToMany
-- Uma bomba pode ter múltiplos tipos de combustível
-- ============================================================

-- 1. Criar a tabela de junção para o relacionamento ManyToMany
CREATE TABLE fuel_pump_fuel_type (
    fuel_pump_id  BIGINT NOT NULL,
    fuel_type_id  BIGINT NOT NULL,
    CONSTRAINT pk_fuel_pump_fuel_type PRIMARY KEY (fuel_pump_id, fuel_type_id),
    CONSTRAINT fk_fuel_pump_fuel_type_pump FOREIGN KEY (fuel_pump_id)
        REFERENCES fuel_pump (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_fuel_pump_fuel_type_type FOREIGN KEY (fuel_type_id)
        REFERENCES fuel_type (id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- 2. Indices para melhorar performance nas consultas
CREATE INDEX idx_fuel_pump_fuel_type_pump ON fuel_pump_fuel_type (fuel_pump_id);
CREATE INDEX idx_fuel_pump_fuel_type_type ON fuel_pump_fuel_type (fuel_type_id);

-- 3. Migrar dados existentes da coluna fuel_type_id para a tabela de junção
-- Cada bomba existente mantém seu combustível original
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id)
SELECT id, fuel_type_id FROM fuel_pump WHERE fuel_type_id IS NOT NULL;

-- 4. Remover a coluna fuel_type_id da tabela fuel_pump (após migração de dados)
-- IMPORTANTE: isso é feito em 2 passos para evitar problemas de integridade
ALTER TABLE fuel_pump DROP CONSTRAINT fk_fuel_pump_fuel_type;
ALTER TABLE fuel_pump DROP COLUMN fuel_type_id;

-- 5. Adicionar coluna updated_at se não existir (para auditoria)
-- Nota: Se já existir, o comando não causará erro (comportamento H2)
ALTER TABLE fuel_pump ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

