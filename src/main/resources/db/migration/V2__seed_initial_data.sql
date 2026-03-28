-- ============================================================
-- V2__seed_initial_data.sql
-- Dados iniciais para demonstração do sistema
-- ============================================================

-- Tipos de combustível iniciais
INSERT INTO fuel_type (name, price_per_liter) VALUES ('Gasolina Comum',   5.890);
INSERT INTO fuel_type (name, price_per_liter) VALUES ('Gasolina Aditivada', 6.190);
INSERT INTO fuel_type (name, price_per_liter) VALUES ('Etanol',            4.290);
INSERT INTO fuel_type (name, price_per_liter) VALUES ('Diesel S10',        6.490);
INSERT INTO fuel_type (name, price_per_liter) VALUES ('GNV',               3.990);

-- Bombas (relacionadas aos combustíveis acima)
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 01 - Gasolina Comum',    1);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 02 - Gasolina Comum',    1);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 03 - Gasolina Aditivada',2);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 04 - Etanol',            3);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 05 - Diesel S10',        4);

-- Abastecimentos de exemplo
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (1, '2025-01-10',  40.000, 235.60);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (2, '2025-01-11',  30.500, 179.55);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (3, '2025-01-12',  25.000, 154.75);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (4, '2025-01-13',  50.000, 214.50);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (5, '2025-01-14', 100.000, 649.00);
