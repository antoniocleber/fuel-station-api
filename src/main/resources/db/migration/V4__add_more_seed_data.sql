-- ============================================================
-- V4__add_more_seed_data.sql
-- Adicionar mais associações ManyToMany e dados de abastecimento
-- ============================================================

-- Adicionar mais tipos de combustível às bombas para ter bombas com combustíveis diversos
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (1, 2); -- Bomba 01: Gasolina Aditivada
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (2, 3); -- Bomba 02: Etanol
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (3, 4); -- Bomba 03: Diesel S10
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (4, 5); -- Bomba 04: GNV
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (5, 1); -- Bomba 05: Gasolina Comum
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (6, 3); -- Bomba 06: Etanol
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (7, 2); -- Bomba 07: Gasolina Aditivada
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (7, 3); -- Bomba 07: Etanol
INSERT INTO fuel_pump_fuel_type (fuel_pump_id, fuel_type_id) VALUES (8, 4); -- Bomba 08: Diesel S10

-- Mais abastecimentos para fevereiro
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (1, '2025-02-10',  38.000, 223.82);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (2, '2025-02-11',  28.000, 164.92);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (3, '2025-02-12',  24.000, 148.56);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (4, '2025-02-13',  48.000, 205.92);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (5, '2025-02-14',  98.000, 636.02);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (6, '2025-02-15',  58.000, 231.42);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (7, '2025-02-16',  33.000, 193.47);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (8, '2025-02-17',  43.000, 185.47);
