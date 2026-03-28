package com.fuelstation.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

/**
 * DTO de entrada para criação ou atualização de uma bomba de combustível.
 *
 * <p>Uma bomba deve ter pelo menos um tipo de combustível associado.</p>
 */
@Data
public class FuelPumpRequest {

    @NotBlank(message = "O nome da bomba é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    private String name;

    @NotEmpty(message = "A bomba deve ter pelo menos um tipo de combustível associado.")
    @Size(min = 1, message = "A bomba deve ter pelo menos um tipo de combustível.")
    private Set<@NotNull(message = "IDs de combustível não podem ser nulos.")
                 @Positive(message = "Os IDs dos combustíveis devem ser números positivos.")
                 Long> fuelTypeIds;
}


