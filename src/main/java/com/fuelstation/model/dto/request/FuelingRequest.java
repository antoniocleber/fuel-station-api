package com.fuelstation.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de entrada para criação ou atualização de um abastecimento.
 *
 * <p>O front-end pode enviar apenas {@code liters} ou apenas {@code totalValue}.
 * O campo ausente será calculado automaticamente a partir do preço unitário
 * do tipo de combustível informado ({@code fuelTypeId}).</p>
 *
 * <ul>
 *   <li>Se {@code liters} informado → {@code totalValue = liters × pricePerLiter}</li>
 *   <li>Se {@code totalValue} informado → {@code liters = totalValue / pricePerLiter}</li>
 * </ul>
 */
@Data
public class FuelingRequest {

    @NotNull(message = "O ID da bomba é obrigatório.")
    @Positive(message = "O ID da bomba deve ser um número positivo.")
    private Long pumpId;

    @NotNull(message = "O ID do tipo de combustível é obrigatório.")
    @Positive(message = "O ID do tipo de combustível deve ser um número positivo.")
    private Long fuelTypeId;

    @NotNull(message = "A data do abastecimento é obrigatória.")
    @PastOrPresent(message = "A data do abastecimento não pode ser futura.")
    private LocalDate fuelingDate;

    @DecimalMin(value = "0.001", inclusive = true, message = "A litragem deve ser maior que zero.")
    @Digits(integer = 7, fraction = 3, message = "A litragem aceita até 7 dígitos inteiros e 3 decimais.")
    private BigDecimal liters;

    @DecimalMin(value = "0.01", inclusive = true, message = "O valor total deve ser maior que zero.")
    @Digits(integer = 8, fraction = 2, message = "O valor total aceita até 8 dígitos inteiros e 2 decimais.")
    private BigDecimal totalValue;
}
