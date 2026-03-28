package com.fuelstation.model.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de saída com os dados completos de um abastecimento.
 */
@Data
public class FuelingResponse {

    private Long id;
    private LocalDate fuelingDate;
    private BigDecimal liters;
    private BigDecimal totalValue;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** Resumo da bomba utilizada no abastecimento. */
    private PumpSummary pump;

    /**
     * Resumo embutido da bomba e dos combustíveis associados.
     * <p>Desde a refatoração para ManyToMany, uma bomba pode ter múltiplos combustíveis.</p>
     */
    @Data
    public static class PumpSummary {
        private Long id;
        private String name;
        /** Lista de tipos de combustível que a bomba pode abastecer. */
        private List<FuelTypeSummary> fuelTypes;
    }

    /**
     * Resumo do tipo de combustível dentro da bomba.
     */
    @Data
    public static class FuelTypeSummary {
        private Long id;
        private String name;
        private BigDecimal pricePerLiter;
    }
}
