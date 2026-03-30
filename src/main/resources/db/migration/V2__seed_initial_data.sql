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
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 01', 1);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 02', 1);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 03', 2);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 04', 3);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 05', 4);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 06', 5);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 07', 1);
INSERT INTO fuel_pump (name, fuel_type_id) VALUES ('Bomba 08', 3);

-- Abastecimentos de exemplo para janeiro
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (1, '2025-01-10',  40.000, 235.60);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (2, '2025-01-11',  30.500, 179.55);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (3, '2025-01-12',  25.000, 154.75);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (4, '2025-01-13',  50.000, 214.50);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (5, '2025-01-14', 100.000, 649.00);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (6, '2025-01-15',  60.000, 239.40);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (7, '2025-01-16',  35.000, 205.15);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (8, '2025-01-17',  45.000, 193.05);

-- Abastecimentos de exemplo para fevereiro
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (1, '2025-02-01',  42.000, 247.38);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (2, '2025-02-02',  32.000, 188.48);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (3, '2025-02-03',  26.000, 160.94);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (4, '2025-02-04',  52.000, 223.08);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (5, '2025-02-05', 105.000, 681.45);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (6, '2025-02-06',  62.000, 247.38);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (7, '2025-02-07',  37.000, 217.33);
INSERT INTO fueling (pump_id, fueling_date, liters, total_value) VALUES (8, '2025-02-08',  47.000, 201.63);
