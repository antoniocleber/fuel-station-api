package com.fuelstation.service;

import com.fuelstation.exception.ResourceNotFoundException;
import com.fuelstation.mapper.FuelingMapper;
import com.fuelstation.model.dto.request.FuelingRequest;
import com.fuelstation.model.dto.response.FuelingResponse;
import com.fuelstation.model.entity.Fueling;
import com.fuelstation.model.entity.FuelPump;
import com.fuelstation.repository.FuelingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço com as regras de negócio para {@link Fueling} (Abastecimentos).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FuelingService {

    private static final String RESOURCE_NAME = "Abastecimento";

    private final FuelingRepository fuelingRepository;
    private final FuelPumpService fuelPumpService;
    private final FuelingMapper fuelingMapper;

    // ── Listagem ───────────────────────────────────────────────────────────────

    /**
     * Retorna todos os abastecimentos com detalhes de bomba e combustível.
     */
    @Transactional(readOnly = true)
    public List<FuelingResponse> findAll() {
        log.debug("Buscando todos os abastecimentos");
        return fuelingMapper.toResponseList(fuelingRepository.findAllWithDetails());
    }

    /**
     * Retorna um abastecimento pelo ID com todos os detalhes.
     *
     * @param id identificador do abastecimento
     * @return DTO de resposta
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional(readOnly = true)
    public FuelingResponse findById(Long id) {
        log.debug("Buscando abastecimento id={}", id);
        Fueling fueling = fuelingRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", id));
        return fuelingMapper.toResponse(fueling);
    }

    /**
     * Filtra abastecimentos por bomba e/ou período.
     *
     * @param pumpId    ID da bomba (nullable)
     * @param startDate data inicial (nullable)
     * @param endDate   data final (nullable)
     * @return lista filtrada de DTOs
     */
    @Transactional(readOnly = true)
    public List<FuelingResponse> findWithFilters(Long pumpId, LocalDate startDate, LocalDate endDate) {
        log.debug("Filtrando abastecimentos: pumpId={}, startDate={}, endDate={}", pumpId, startDate, endDate);
        return fuelingMapper.toResponseList(
                fuelingRepository.findWithFilters(pumpId, startDate, endDate)
        );
    }

    // ── Criação ────────────────────────────────────────────────────────────────

    /**
     * Registra um novo abastecimento.
     *
     * @param request dados de entrada
     * @return DTO do abastecimento criado
     * @throws ResourceNotFoundException se a bomba informada não existir
     */
    @Transactional
    public FuelingResponse create(FuelingRequest request) {
        log.info("Registrando abastecimento na bomba id={}", request.getPumpId());
        FuelPump pump = fuelPumpService.findEntityById(request.getPumpId());

        Fueling entity = fuelingMapper.toEntity(request);
        entity.setPump(pump);

        Fueling saved = fuelingRepository.save(entity);

        // Recarrega com join fetch para montar o DTO completo
        return findById(saved.getId());
    }

    // ── Atualização ────────────────────────────────────────────────────────────

    /**
     * Atualiza um abastecimento existente.
     *
     * @param id      ID do abastecimento
     * @param request novos dados
     * @return DTO atualizado
     * @throws ResourceNotFoundException se abastecimento ou bomba não forem encontrados
     */
    @Transactional
    public FuelingResponse update(Long id, FuelingRequest request) {
        log.info("Atualizando abastecimento id={}", id);

        Fueling entity = fuelingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", id));

        FuelPump pump = fuelPumpService.findEntityById(request.getPumpId());

        fuelingMapper.updateEntityFromRequest(request, entity);
        entity.setPump(pump);

        fuelingRepository.save(entity);
        log.info("Abastecimento id={} atualizado", id);

        return findById(id);
    }

    // ── Exclusão ───────────────────────────────────────────────────────────────

    /**
     * Remove um abastecimento pelo ID.
     *
     * @param id ID do abastecimento
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional
    public void delete(Long id) {
        log.info("Removendo abastecimento id={}", id);
        if (!fuelingRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURCE_NAME, "id", id);
        }
        fuelingRepository.deleteById(id);
        log.info("Abastecimento id={} removido", id);
    }
}
