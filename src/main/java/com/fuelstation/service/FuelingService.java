package com.fuelstation.service;

import com.fuelstation.exception.BusinessException;
import com.fuelstation.exception.ResourceNotFoundException;
import com.fuelstation.mapper.FuelingMapper;
import com.fuelstation.model.dto.request.FuelingRequest;
import com.fuelstation.model.dto.response.FuelingResponse;
import com.fuelstation.model.dto.response.PageResponse;
import com.fuelstation.model.entity.Fueling;
import com.fuelstation.model.entity.FuelPump;
import com.fuelstation.model.entity.FuelType;
import com.fuelstation.repository.FuelingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço com as regras de negócio para {@link Fueling} (Abastecimentos).
 *
 * <p>Ao criar ou atualizar um abastecimento, o front-end pode enviar apenas
 * {@code liters} ou apenas {@code totalValue}. O valor ausente é calculado
 * automaticamente a partir do preço unitário do tipo de combustível informado.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FuelingService {

    private static final String RESOURCE_NAME = "Abastecimento";

    private final FuelingRepository fuelingRepository;
    private final FuelPumpService fuelPumpService;
    private final FuelTypeService fuelTypeService;
    private final FuelingMapper fuelingMapper;

    // ── Listagem ───────────────────────────────────────────────────────────────

    /**
     * Retorna todos os abastecimentos com detalhes de bomba e combustível.
     */
    @Transactional(readOnly = true)
    public PageResponse<FuelingResponse> findAll(Pageable pageable) {
        log.debug("Buscando abastecimentos paginados: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return getPageResponse(fuelingRepository.findPageIds(pageable), pageable);
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
    public PageResponse<FuelingResponse> findWithFilters(
            Long pumpId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        log.debug("Filtrando abastecimentos: pumpId={}, startDate={}, endDate={}", pumpId, startDate, endDate);
        return getPageResponse(
                fuelingRepository.findPageIdsWithFilters(pumpId, startDate, endDate, pageable),
                pageable
        );
    }

    // ── Criação ────────────────────────────────────────────────────────────────

    /**
     * Registra um novo abastecimento.
     *
     * <p>Calcula automaticamente {@code liters} ou {@code totalValue} quando
     * apenas um dos dois é informado pelo front-end.</p>
     *
     * @param request dados de entrada
     * @return DTO do abastecimento criado
     * @throws ResourceNotFoundException se a bomba ou tipo de combustível não existir
     * @throws BusinessException se nenhum dos dois (liters/totalValue) for informado,
     *         ou se o tipo de combustível não pertencer à bomba informada
     */
    @Transactional
    public FuelingResponse create(FuelingRequest request) {
        log.info("Registrando abastecimento na bomba id={}", request.getPumpId());
        FuelPump pump = fuelPumpService.findEntityById(request.getPumpId());
        FuelType fuelType = fuelTypeService.findEntityById(request.getFuelTypeId());

        validateFuelTypeInPump(pump, fuelType);
        calculateMissingValues(request, fuelType);

        Fueling entity = fuelingMapper.toEntity(request);
        entity.setPump(pump);
        entity.setFuelType(fuelType);

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
        FuelType fuelType = fuelTypeService.findEntityById(request.getFuelTypeId());

        validateFuelTypeInPump(pump, fuelType);
        calculateMissingValues(request, fuelType);

        fuelingMapper.updateEntityFromRequest(request, entity);
        entity.setPump(pump);
        entity.setFuelType(fuelType);

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

    // ── Helpers internos ───────────────────────────────────────────────────────

    /**
     * Valida que o tipo de combustível pertence à bomba informada.
     *
     * @param pump     bomba do abastecimento
     * @param fuelType tipo de combustível
     * @throws BusinessException se o tipo de combustível não estiver associado à bomba
     */
    private void validateFuelTypeInPump(FuelPump pump, FuelType fuelType) {
        boolean pumpHasFuelType = pump.getFuelTypes().stream()
                .anyMatch(ft -> ft.getId().equals(fuelType.getId()));

        if (!pumpHasFuelType) {
            throw new BusinessException(String.format(
                    "O tipo de combustível '%s' (id=%d) não está associado à bomba '%s' (id=%d).",
                    fuelType.getName(), fuelType.getId(), pump.getName(), pump.getId()));
        }
    }

    /**
     * Calcula o valor ausente (liters ou totalValue) com base no preço unitário.
     *
     * <ul>
     *   <li>Se apenas {@code liters}: totalValue = liters × pricePerLiter (2 casas)</li>
     *   <li>Se apenas {@code totalValue}: liters = totalValue / pricePerLiter (3 casas)</li>
     *   <li>Se ambos informados: mantém os valores do request sem alteração</li>
     *   <li>Se nenhum informado: lança BusinessException</li>
     * </ul>
     */
    private void calculateMissingValues(FuelingRequest request, FuelType fuelType) {
        BigDecimal pricePerLiter = fuelType.getPricePerLiter();

        boolean hasLiters = request.getLiters() != null;
        boolean hasTotalValue = request.getTotalValue() != null;

        if (!hasLiters && !hasTotalValue) {
            throw new BusinessException(
                    "É obrigatório informar a quantidade de litros ou o valor total do abastecimento.");
        }

        if (hasLiters && !hasTotalValue) {
            // totalValue = liters × pricePerLiter
            BigDecimal calculatedTotal = request.getLiters()
                    .multiply(pricePerLiter)
                    .setScale(2, RoundingMode.HALF_UP);
            request.setTotalValue(calculatedTotal);
            log.debug("Valor total calculado: {} (liters={} × price={})",
                    calculatedTotal, request.getLiters(), pricePerLiter);
        } else if (!hasLiters && hasTotalValue) {
            // liters = totalValue / pricePerLiter
            BigDecimal calculatedLiters = request.getTotalValue()
                    .divide(pricePerLiter, 3, RoundingMode.HALF_UP);
            request.setLiters(calculatedLiters);
            log.debug("Litros calculados: {} (totalValue={} / price={})",
                    calculatedLiters, request.getTotalValue(), pricePerLiter);
        }
    }

    private PageResponse<FuelingResponse> getPageResponse(Page<Long> idPage, Pageable pageable) {
        if (idPage.isEmpty()) {
            return PageResponse.from(Page.empty(pageable));
        }

        List<Long> orderedIds = idPage.getContent();
        Map<Long, Integer> idOrder = toOrderMap(orderedIds);

        List<FuelingResponse> content = fuelingRepository.findAllByIdInWithDetails(orderedIds).stream()
                .sorted(Comparator.comparingInt(fueling -> idOrder.getOrDefault(fueling.getId(), Integer.MAX_VALUE)))
                .map(fuelingMapper::toResponse)
                .toList();

        return PageResponse.from(new PageImpl<>(content, pageable, idPage.getTotalElements()));
    }

    private Map<Long, Integer> toOrderMap(List<Long> orderedIds) {
        return orderedIds.stream()
                .collect(Collectors.toMap(id -> id, orderedIds::indexOf));
    }
}
