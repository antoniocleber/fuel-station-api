package com.fuelstation.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de saída com os dados de uma bomba de combustível.
 *
 * <p>Uma bomba pode ter múltiplos tipos de combustível associados.</p>
 */
@Data
public class FuelPumpResponse {

    private Long id;
    private String name;

    /** Lista de tipos de combustível que esta bomba pode abastecer. */
    private List<FuelTypeSummary> fuelTypes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Resumo embutido do tipo de combustível
     * (evita exposição desnecessária de dados).
     */
    @Data
    public static class FuelTypeSummary {
        private Long id;
        private String name;
        private BigDecimal pricePerLiter;
    }
}


