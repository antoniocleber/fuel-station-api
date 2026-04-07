package com.fuelstation.service;

import com.fuelstation.model.dto.response.ReportResponse;
import com.fuelstation.model.entity.Fueling;
import com.fuelstation.model.entity.FuelPump;
import com.fuelstation.model.entity.FuelType;
import com.fuelstation.repository.FuelingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço responsável por gerar relatórios de abastecimentos
 * agrupados por bomba de combustível.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final FuelingRepository fuelingRepository;

    /**
     * Gera o relatório de abastecimentos agrupados por bomba.
     *
     * @param pumpId    filtra por bomba (nullable)
     * @param startDate data inicial (nullable)
     * @param endDate   data final (nullable)
     * @return relatório com dados agrupados e totalizados
     */
    @Transactional(readOnly = true)
    public ReportResponse generateReport(Long pumpId, LocalDate startDate, LocalDate endDate) {
        log.debug("Gerando relatório: pumpId={}, startDate={}, endDate={}", pumpId, startDate, endDate);

        List<Fueling> fuelings = fuelingRepository.findWithFilters(pumpId, startDate, endDate);

        // Agrupar por bomba mantendo a ordem de inserção
        Map<Long, List<Fueling>> fuelingsByPump = fuelings.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getPump().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<ReportResponse.PumpReport> pumpReports = new ArrayList<>();
        BigDecimal grandTotalLiters = BigDecimal.ZERO;
        BigDecimal grandTotalValue = BigDecimal.ZERO;
        int grandTotalFuelings = 0;

        for (Map.Entry<Long, List<Fueling>> entry : fuelingsByPump.entrySet()) {
            List<Fueling> pumpFuelings = entry.getValue();
            FuelPump pump = pumpFuelings.get(0).getPump();

            List<ReportResponse.FuelTypeSummary> fuelTypeSummaries = pump.getFuelTypes().stream()
                    .map(this::toFuelTypeSummary)
                    .collect(Collectors.toList());

            List<ReportResponse.FuelingDetail> fuelingDetails = pumpFuelings.stream()
                    .map(this::toFuelingDetail)
                    .collect(Collectors.toList());

            BigDecimal pumpTotalLiters = pumpFuelings.stream()
                    .map(Fueling::getLiters)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal pumpTotalValue = pumpFuelings.stream()
                    .map(Fueling::getTotalValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            ReportResponse.PumpReport pumpReport = ReportResponse.PumpReport.builder()
                    .pumpId(pump.getId())
                    .pumpName(pump.getName())
                    .fuelTypes(fuelTypeSummaries)
                    .fuelings(fuelingDetails)
                    .totalLiters(pumpTotalLiters)
                    .totalValue(pumpTotalValue)
                    .fuelingsCount(pumpFuelings.size())
                    .build();

            pumpReports.add(pumpReport);

            grandTotalLiters = grandTotalLiters.add(pumpTotalLiters);
            grandTotalValue = grandTotalValue.add(pumpTotalValue);
            grandTotalFuelings += pumpFuelings.size();
        }

        return ReportResponse.builder()
                .pumps(pumpReports)
                .totalLiters(grandTotalLiters)
                .totalValue(grandTotalValue)
                .totalFuelings(grandTotalFuelings)
                .build();
    }

    private ReportResponse.FuelTypeSummary toFuelTypeSummary(FuelType fuelType) {
        return ReportResponse.FuelTypeSummary.builder()
                .id(fuelType.getId())
                .name(fuelType.getName())
                .pricePerLiter(fuelType.getPricePerLiter())
                .build();
    }

    private ReportResponse.FuelingDetail toFuelingDetail(Fueling fueling) {
        ReportResponse.FuelTypeSummary fuelTypeSummary = null;
        if (fueling.getFuelType() != null) {
            fuelTypeSummary = toFuelTypeSummary(fueling.getFuelType());
        }

        return ReportResponse.FuelingDetail.builder()
                .id(fueling.getId())
                .fuelingDate(fueling.getFuelingDate())
                .liters(fueling.getLiters())
                .totalValue(fueling.getTotalValue())
                .fuelType(fuelTypeSummary)
                .createdAt(fueling.getCreatedAt())
                .build();
    }
}
