package com.fuelstation.controller;

import com.fuelstation.model.dto.request.FuelPumpRequest;
import com.fuelstation.model.dto.response.ApiErrorResponse;
import com.fuelstation.model.dto.response.FuelPumpResponse;
import com.fuelstation.service.FuelPumpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para operações de Bombas de Combustível.
 *
 * <p>Expõe os endpoints CRUD em {@code /api/v1/fuel-pumps}.</p>
 * <p>Regra de negócio: Uma bomba pode ter múltiplos tipos de combustível associados.</p>
 */
@RestController
@RequestMapping("/api/v1/fuel-pumps")
@RequiredArgsConstructor
@Tag(name = "Bombas de Combustível", description = "CRUD de bombas do posto, cada bomba pode ter múltiplos tipos de combustível")
public class FuelPumpController {

    private final FuelPumpService fuelPumpService;

    @GetMapping
    @Operation(summary = "Lista todas as bombas com seus tipos de combustível")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<FuelPumpResponse>> findAll() {
        return ResponseEntity.ok(fuelPumpService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca uma bomba pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bomba encontrada"),
        @ApiResponse(responseCode = "404", description = "Bomba não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelPumpResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelPumpService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Cria uma nova bomba de combustível com um ou mais tipos de combustível")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Bomba criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: fuelTypeIds vazio ou combustível não encontrado)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Um ou mais tipos de combustível não encontrados",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Nome de bomba já em uso",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelPumpResponse> create(@Valid @RequestBody FuelPumpRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelPumpService.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza uma bomba existente e seus tipos de combustível")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bomba atualizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos (ex: fuelTypeIds vazio)",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Bomba ou um ou mais combustíveis não encontrados",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Nome de bomba já em uso",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelPumpResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FuelPumpRequest request) {
        return ResponseEntity.ok(fuelPumpService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove uma bomba de combustível")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Bomba removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Bomba não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Existem abastecimentos vinculados",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelPumpService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
