package com.fuelstation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Fuel Station.
 *
 * <p>Ponto de entrada da API RESTful para gerenciamento de abastecimentos
 * em posto de combustível.</p>
 */
@SpringBootApplication
public class FuelStationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuelStationApplication.class, args);
    }
}
