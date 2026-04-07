-- ============================================================
-- V5__add_fuel_type_to_fueling.sql
-- Adicionar coluna fuel_type_id na tabela fueling para registrar
-- qual tipo de combustível foi utilizado em cada abastecimento.
-- ============================================================

-- 1. Adicionar coluna fuel_type_id (inicialmente nullable para permitir migração)
ALTER TABLE fueling ADD COLUMN fuel_type_id BIGINT NULL;

-- 2. Migrar dados existentes: associar cada abastecimento ao primeiro
--    tipo de combustível da bomba utilizada
UPDATE fueling f
SET f.fuel_type_id = (
    SELECT MIN(fpft.fuel_type_id)
    FROM fuel_pump_fuel_type fpft
    WHERE fpft.fuel_pump_id = f.pump_id
);

-- 3. Tornar a coluna NOT NULL após migração
ALTER TABLE fueling ALTER COLUMN fuel_type_id SET NOT NULL;

-- 4. Adicionar constraint de chave estrangeira
ALTER TABLE fueling ADD CONSTRAINT fk_fueling_fuel_type
    FOREIGN KEY (fuel_type_id) REFERENCES fuel_type (id)
    ON DELETE RESTRICT ON UPDATE CASCADE;

-- 5. Índice para melhorar performance
CREATE INDEX idx_fueling_fuel_type_id ON fueling (fuel_type_id);
