package com.fuelstation.controller;

import com.fuelstation.model.dto.response.ReportResponse;
import com.fuelstation.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controller REST para relatórios de abastecimentos.
 *
 * <p>Retorna dados agrupados por bomba de combustível com totais
 * consolidados para uso em telas de relatório do front-end.</p>
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Relatórios de abastecimentos agrupados por bomba de combustível")
public class ReportController {

    private final ReportService reportService;

    /**
     * Gera relatório de abastecimentos agrupados por bomba de combustível.
     *
     * @param pumpId    filtra por bomba (opcional)
     * @param startDate data inicial do período (opcional)
     * @param endDate   data final do período (opcional)
     * @return relatório com dados agrupados e totalizados
     */
    @GetMapping("/fuelings")
    @Operation(summary = "Relatório de abastecimentos por bomba de combustível",
            description = "Retorna os abastecimentos agrupados por bomba com totais de litros e valores")
    @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso")
    public ResponseEntity<ReportResponse> getFuelingsReport(
            @Parameter(description = "ID da bomba para filtrar")
            @RequestParam(required = false) Long pumpId,

            @Parameter(description = "Data inicial do período (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Data final do período (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(reportService.generateReport(pumpId, startDate, endDate));
    }
}
