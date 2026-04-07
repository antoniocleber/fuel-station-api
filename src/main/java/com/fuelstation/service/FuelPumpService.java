package com.fuelstation.service;

import com.fuelstation.exception.BusinessException;
import com.fuelstation.exception.ConflictException;
import com.fuelstation.exception.ResourceNotFoundException;
import com.fuelstation.mapper.FuelPumpMapper;
import com.fuelstation.model.dto.request.FuelPumpRequest;
import com.fuelstation.model.dto.response.FuelPumpResponse;
import com.fuelstation.model.dto.response.PageResponse;
import com.fuelstation.model.entity.FuelPump;
import com.fuelstation.model.entity.FuelType;
import com.fuelstation.repository.FuelPumpRepository;
import com.fuelstation.repository.FuelingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Serviço com as regras de negócio para {@link FuelPump}.
 *
 * <p>Regra de negócio: Uma bomba deve ter pelo menos um tipo de combustível associado.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FuelPumpService {

    private static final String RESOURCE_NAME = "Bomba de Combustível";

    private final FuelPumpRepository fuelPumpRepository;
    private final FuelingRepository fuelingRepository;
    private final FuelTypeService fuelTypeService;
    private final FuelPumpMapper fuelPumpMapper;

    // ── Listagem ───────────────────────────────────────────────────────────────

    /**
     * Retorna todas as bombas com seus combustíveis associados.
     */
    @Transactional(readOnly = true)
    public PageResponse<FuelPumpResponse> findAll(Pageable pageable) {
        log.debug("Buscando bombas paginadas: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Long> idPage = fuelPumpRepository.findPageIds(pageable);
        if (idPage.isEmpty()) {
            return PageResponse.from(Page.empty(pageable));
        }

        List<Long> orderedIds = idPage.getContent();
        Map<Long, Integer> idOrder = toOrderMap(orderedIds);

        List<FuelPumpResponse> content = fuelPumpRepository.findAllByIdInWithFuelTypes(orderedIds).stream()
                .sorted(Comparator.comparingInt(pump -> idOrder.getOrDefault(pump.getId(), Integer.MAX_VALUE)))
                .map(fuelPumpMapper::toResponse)
                .toList();

        return PageResponse.from(new PageImpl<>(content, pageable, idPage.getTotalElements()));
    }

    /**
     * Retorna uma bomba pelo ID.
     *
     * @param id identificador da bomba
     * @return DTO de resposta
     * @throws ResourceNotFoundException se não encontrada
     */
    @Transactional(readOnly = true)
    public FuelPumpResponse findById(Long id) {
        log.debug("Buscando bomba id={}", id);
        return fuelPumpMapper.toResponse(findEntityById(id));
    }

    // ── Criação ────────────────────────────────────────────────────────────────

    /**
     * Cria uma nova bomba de combustível com múltiplos tipos de combustível.
     *
     * @param request dados de entrada (deve conter fuelTypeIds não vazio)
     * @return DTO da bomba criada
     * @throws ConflictException         se o nome já estiver em uso
     * @throws ResourceNotFoundException se algum combustível informado não existir
     * @throws BusinessException         se nenhum combustível for informado
     */
    @Transactional
    public FuelPumpResponse create(FuelPumpRequest request) {
        log.info("Criando bomba: {} com {} tipo(s) de combustível", request.getName(), request.getFuelTypeIds().size());
        validateUniqueName(request.getName(), null);
        
        // Validação: deve ter ao menos 1 combustível
        if (request.getFuelTypeIds().isEmpty()) {
            throw new BusinessException("Uma bomba deve ter pelo menos um tipo de combustível associado.");
        }

        // Carregar todos os combustíveis informados
        Set<FuelType> fuelTypes = loadFuelTypes(request.getFuelTypeIds());

        FuelPump entity = fuelPumpMapper.toEntity(request);
        entity.setFuelTypes(fuelTypes);

        FuelPump saved = fuelPumpRepository.save(entity);
        log.info("Bomba criada com id={} associada a {} tipo(s) de combustível", saved.getId(), fuelTypes.size());
        return fuelPumpMapper.toResponse(saved);
    }

    // ── Atualização ────────────────────────────────────────────────────────────

    /**
     * Atualiza dados de uma bomba existente.
     *
     * <p>Ao editar, os novos tipos de combustível são <strong>adicionados</strong>
     * aos existentes (merge), em vez de sobrescrever. Se o tipo de combustível
     * já estiver associado à bomba, ele é ignorado (sem duplicatas).</p>
     *
     * @param id      ID da bomba
     * @param request novos dados (incluindo tipos de combustível a adicionar)
     * @return DTO atualizado
     * @throws BusinessException se fuelTypeIds is empty
     */
    @Transactional
    public FuelPumpResponse update(Long id, FuelPumpRequest request) {
        log.info("Atualizando bomba id={} com {} tipo(s) de combustível", id, request.getFuelTypeIds().size());
        FuelPump entity = findEntityById(id);
        validateUniqueName(request.getName(), id);
        
        // Validação: deve ter ao menos 1 combustível
        if (request.getFuelTypeIds().isEmpty()) {
            throw new BusinessException("Uma bomba deve ter pelo menos um tipo de combustível associado.");
        }

        // Carregar os novos tipos de combustível informados
        Set<FuelType> newFuelTypes = loadFuelTypes(request.getFuelTypeIds());

        fuelPumpMapper.updateEntityFromRequest(request, entity);

        // Merge: adicionar novos tipos aos existentes (sem duplicatas - Set garante unicidade)
        Set<FuelType> existingFuelTypes = entity.getFuelTypes();
        existingFuelTypes.addAll(newFuelTypes);
        entity.setFuelTypes(existingFuelTypes);

        FuelPump saved = fuelPumpRepository.save(entity);
        log.info("Bomba id={} atualizada, total de {} tipo(s) de combustível", id, saved.getFuelTypes().size());
        return fuelPumpMapper.toResponse(saved);
    }

    // ── Exclusão ───────────────────────────────────────────────────────────────

    /**
     * Remove uma bomba pelo ID.
     *
     * @param id ID da bomba
     * @throws BusinessException se houver abastecimentos registrados para a bomba
     */
    @Transactional
    public void delete(Long id) {
        log.info("Removendo bomba id={}", id);
        findEntityById(id);

        if (fuelingRepository.existsByPumpId(id)) {
            throw new BusinessException(
                    "Não é possível excluir a bomba pois existem abastecimentos registrados para ela. " +
                    "Remova os abastecimentos antes de excluir a bomba."
            );
        }

        fuelPumpRepository.deleteById(id);
        log.info("Bomba id={} removida", id);
    }

    // ── Helpers internos ───────────────────────────────────────────────────────

    /**
     * Busca entidade pelo ID ou lança {@link ResourceNotFoundException}.
     */
    public FuelPump findEntityById(Long id) {
        return fuelPumpRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", id));
    }

    private void validateUniqueName(String name, Long id) {
        boolean duplicado = (id == null)
                ? fuelPumpRepository.existsByName(name)
                : fuelPumpRepository.existsByNameAndIdNot(name, id);

        if (duplicado) {
            throw new ConflictException(
                    String.format("Já existe uma bomba com o nome '%s'.", name));
        }
    }

    /**
     * Carrega todos os tipos de combustível pelos IDs fornecidos.
     *
     * @param fuelTypeIds Set de IDs dos combustíveis
     * @return Set de FuelType encontrados
     * @throws ResourceNotFoundException se algum ID não encontrar um combustível
     */
    private Set<FuelType> loadFuelTypes(Set<Long> fuelTypeIds) {
        Set<FuelType> fuelTypes = new HashSet<>();
        for (Long fuelTypeId : fuelTypeIds) {
            FuelType fuelType = fuelTypeService.findEntityById(fuelTypeId);
            fuelTypes.add(fuelType);
        }
        return fuelTypes;
    }

    private Map<Long, Integer> toOrderMap(List<Long> orderedIds) {
        return orderedIds.stream()
                .collect(Collectors.toMap(id -> id, orderedIds::indexOf));
    }
}

