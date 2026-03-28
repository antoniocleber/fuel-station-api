package com.fuelstation.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada para criação ou atualização de um abastecimento.
 */
@Data
public class FuelingRequest {

    @NotNull(message = "O ID da bomba é obrigatório.")
    @Positive(message = "O ID da bomba deve ser um número positivo.")
    private Long pumpId;

    @NotNull(message = "A data do abastecimento é obrigatória.")
    @PastOrPresent(message = "A data do abastecimento não pode ser futura.")
    private LocalDate fuelingDate;

    @NotNull(message = "A litragem é obrigatória.")
    @DecimalMin(value = "0.001", inclusive = true, message = "A litragem deve ser maior que zero.")
    @Digits(integer = 7, fraction = 3, message = "A litragem aceita até 7 dígitos inteiros e 3 decimais.")
    private BigDecimal liters;

    @NotNull(message = "O valor total é obrigatório.")
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor total deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O valor total aceita até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal totalValue;
}
