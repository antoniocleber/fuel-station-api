package com.fuelstation.controller;

import com.fuelstation.model.dto.request.FuelingRequest;
import com.fuelstation.model.dto.response.ApiErrorResponse;
import com.fuelstation.model.dto.response.FuelingResponse;
import com.fuelstation.model.dto.response.PageResponse;
import com.fuelstation.service.FuelingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller REST para operações de Abastecimentos.
 *
 * <p>Expõe os endpoints CRUD em {@code /api/v1/fuelings}, com suporte
 * a filtros opcionais de bomba e período de data.</p>
 */
@RestController
@RequestMapping("/api/v1/fuelings")
@RequiredArgsConstructor
@Tag(name = "Abastecimentos", description = "CRUD de registros de abastecimento realizados no posto")
public class FuelingController {

    private final FuelingService fuelingService;

    /**
     * Lista abastecimentos com filtros opcionais.
     *
     * @param pumpId    filtra por bomba (opcional)
     * @param startDate data inicial do período (opcional, formato yyyy-MM-dd)
     * @param endDate   data final do período (opcional, formato yyyy-MM-dd)
     */
    @GetMapping
    @Operation(summary = "Lista abastecimentos paginados (com filtros opcionais)",
            description = "Filtros: pumpId, startDate, endDate. Paginação: page (default 0), size (default 20), sort (default fuelingDate,desc)")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<PageResponse<FuelingResponse>> findAll(
            @Parameter(description = "ID da bomba para filtrar")
            @RequestParam(required = false) Long pumpId,

            @Parameter(description = "Data inicial do período (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "Data final do período (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @ParameterObject
            @PageableDefault(size = 20, sort = "fuelingDate", direction = Sort.Direction.DESC)
            Pageable pageable) {

        if (pumpId != null || startDate != null || endDate != null) {
            return ResponseEntity.ok(fuelingService.findWithFilters(pumpId, startDate, endDate, pageable));
        }
        return ResponseEntity.ok(fuelingService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um abastecimento pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Abastecimento encontrado"),
        @ApiResponse(responseCode = "404", description = "Abastecimento não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelingResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelingService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registra um novo abastecimento")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Abastecimento registrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Bomba não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelingResponse> create(@Valid @RequestBody FuelingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelingService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um abastecimento existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Abastecimento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Abastecimento ou bomba não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelingResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FuelingRequest request) {
        return ResponseEntity.ok(fuelingService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um abastecimento")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Abastecimento removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Abastecimento não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
