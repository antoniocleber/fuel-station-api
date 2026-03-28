package com.fuelstation.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de saída com os dados de um tipo de combustível.
 */
@Data
public class FuelTypeResponse {

    private Long id;
    private String name;
    private BigDecimal pricePerLiter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
