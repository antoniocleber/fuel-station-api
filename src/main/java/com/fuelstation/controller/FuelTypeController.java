package com.fuelstation.controller;

import com.fuelstation.model.dto.request.FuelTypeRequest;
import com.fuelstation.model.dto.response.ApiErrorResponse;
import com.fuelstation.model.dto.response.FuelTypeResponse;
import com.fuelstation.service.FuelTypeService;
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
 * Controller REST para operações de Tipos de Combustível.
 *
 * <p>Expõe os endpoints CRUD em {@code /api/v1/fuel-types}.</p>
 */
@RestController
@RequestMapping("/api/v1/fuel-types")
@RequiredArgsConstructor
@Tag(name = "Tipos de Combustível", description = "CRUD de tipos de combustível cadastrados no posto")
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    // ── GET /api/v1/fuel-types ────────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Lista todos os tipos de combustível")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<FuelTypeResponse>> findAll() {
        return ResponseEntity.ok(fuelTypeService.findAll());
    }

    // ── GET /api/v1/fuel-types/{id} ───────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Busca um tipo de combustível pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combustível encontrado"),
        @ApiResponse(responseCode = "404", description = "Combustível não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelTypeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fuelTypeService.findById(id));
    }

    // ── POST /api/v1/fuel-types ───────────────────────────────────────────────
    @PostMapping
    @Operation(summary = "Cria um novo tipo de combustível")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Combustível criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Nome já cadastrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelTypeResponse> create(@Valid @RequestBody FuelTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelTypeService.create(request));
    }

    // ── PUT /api/v1/fuel-types/{id} ───────────────────────────────────────────
    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um tipo de combustível existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Combustível atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Combustível não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Nome já em uso por outro combustível",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<FuelTypeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody FuelTypeRequest request) {
        return ResponseEntity.ok(fuelTypeService.update(id, request));
    }

    // ── DELETE /api/v1/fuel-types/{id} ────────────────────────────────────────
    @DeleteMapping("/{id}")
    @Operation(summary = "Remove um tipo de combustível")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Combustível removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Combustível não encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "422", description = "Existem bombas associadas; remoção bloqueada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fuelTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
