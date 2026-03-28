package com.fuelstation.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de entrada para criação ou atualização de um tipo de combustível.
 */
@Data
public class FuelTypeRequest {

    @NotBlank(message = "O nome do combustível é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    private String name;

    @NotNull(message = "O preço por litro é obrigatório.")
    @DecimalMin(value = "0.001", inclusive = true, message = "O preço por litro deve ser maior que zero.")
    @Digits(integer = 7, fraction = 3, message = "O preço aceita até 7 dígitos inteiros e 3 decimais.")
    private BigDecimal pricePerLiter;
}
