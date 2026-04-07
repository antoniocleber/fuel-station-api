package com.fuelstation.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para o relatório de abastecimentos agrupados por bomba.
 *
 * <p>Cada item contém os dados da bomba, seus tipos de combustível e a lista
 * de abastecimentos realizados, com totais consolidados.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    /** Lista de bombas com seus abastecimentos. */
    private List<PumpReport> pumps;

    /** Total geral de litros de todos os abastecimentos. */
    private BigDecimal totalLiters;

    /** Valor total geral de todos os abastecimentos. */
    private BigDecimal totalValue;

    /** Quantidade total de abastecimentos. */
    private int totalFuelings;

    /**
     * Relatório individual de uma bomba com seus abastecimentos.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PumpReport {
        private Long pumpId;
        private String pumpName;

        /** Tipos de combustível disponíveis nesta bomba. */
        private List<FuelTypeSummary> fuelTypes;

        /** Abastecimentos realizados nesta bomba. */
        private List<FuelingDetail> fuelings;

        /** Total de litros abastecidos nesta bomba. */
        private BigDecimal totalLiters;

        /** Valor total dos abastecimentos nesta bomba. */
        private BigDecimal totalValue;

        /** Quantidade de abastecimentos nesta bomba. */
        private int fuelingsCount;
    }

    /**
     * Detalhes de um abastecimento individual no relatório.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FuelingDetail {
        private Long id;
        private LocalDate fuelingDate;
        private BigDecimal liters;
        private BigDecimal totalValue;
        private FuelTypeSummary fuelType;
        private LocalDateTime createdAt;
    }

    /**
     * Resumo do tipo de combustível.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FuelTypeSummary {
        private Long id;
        private String name;
        private BigDecimal pricePerLiter;
    }
}
