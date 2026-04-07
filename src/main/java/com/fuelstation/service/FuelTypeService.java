package com.fuelstation.service;

import com.fuelstation.exception.BusinessException;
import com.fuelstation.exception.ConflictException;
import com.fuelstation.exception.ResourceNotFoundException;
import com.fuelstation.mapper.FuelTypeMapper;
import com.fuelstation.model.dto.request.FuelTypeRequest;
import com.fuelstation.model.dto.response.FuelTypeResponse;
import com.fuelstation.model.dto.response.PageResponse;
import com.fuelstation.model.entity.FuelType;
import com.fuelstation.repository.FuelPumpRepository;
import com.fuelstation.repository.FuelTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço com as regras de negócio para {@link FuelType}.
 *
 * <p>Toda operação de escrita é transacional para garantir consistência.
 * Operações de leitura usam {@code readOnly = true} para otimização do Hibernate.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FuelTypeService {

    private static final String RESOURCE_NAME = "Tipo de Combustível";

    private final FuelTypeRepository fuelTypeRepository;
    private final FuelPumpRepository fuelPumpRepository;
    private final FuelTypeMapper fuelTypeMapper;

    // ── Listagem ───────────────────────────────────────────────────────────────

    /**
     * Retorna todos os tipos de combustível cadastrados.
     *
     * @return lista de DTOs de resposta
     */
    @Transactional(readOnly = true)
    public PageResponse<FuelTypeResponse> findAll(Pageable pageable) {
        log.debug("Buscando tipos de combustível paginados: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<FuelTypeResponse> page = fuelTypeRepository.findAll(pageable)
                .map(fuelTypeMapper::toResponse);
        return PageResponse.from(page);
    }

    /**
     * Retorna um tipo de combustível pelo ID.
     *
     * @param id identificador do combustível
     * @return DTO de resposta
     * @throws ResourceNotFoundException se não encontrado
     */
    @Transactional(readOnly = true)
    public FuelTypeResponse findById(Long id) {
        log.debug("Buscando tipo de combustível id={}", id);
        return fuelTypeMapper.toResponse(findEntityById(id));
    }

    // ── Criação ────────────────────────────────────────────────────────────────

    /**
     * Cria um novo tipo de combustível.
     *
     * @param request dados de entrada
     * @return DTO do combustível criado
     * @throws ConflictException se o nome já estiver em uso
     */
    @Transactional
    public FuelTypeResponse create(FuelTypeRequest request) {
        log.info("Criando tipo de combustível: {}", request.getName());
        validateUniqueName(request.getName(), null);

        FuelType entity = fuelTypeMapper.toEntity(request);
        FuelType saved = fuelTypeRepository.save(entity);

        log.info("Tipo de combustível criado com id={}", saved.getId());
        return fuelTypeMapper.toResponse(saved);
    }

    // ── Atualização ────────────────────────────────────────────────────────────

    /**
     * Atualiza um tipo de combustível existente.
     *
     * @param id      ID do combustível a atualizar
     * @param request novos dados
     * @return DTO atualizado
     * @throws ResourceNotFoundException se não encontrado
     * @throws ConflictException         se o novo nome já estiver em uso por outro registro
     */
    @Transactional
    public FuelTypeResponse update(Long id, FuelTypeRequest request) {
        log.info("Atualizando tipo de combustível id={}", id);
        FuelType entity = findEntityById(id);
        validateUniqueName(request.getName(), id);

        fuelTypeMapper.updateEntityFromRequest(request, entity);
        FuelType saved = fuelTypeRepository.save(entity);

        log.info("Tipo de combustível id={} atualizado", id);
        return fuelTypeMapper.toResponse(saved);
    }

    // ── Exclusão ───────────────────────────────────────────────────────────────

    /**
     * Remove um tipo de combustível pelo ID.
     *
     * @param id ID do combustível a remover
     * @throws ResourceNotFoundException se não encontrado
     * @throws BusinessException         se houver bombas associadas ao combustível
     */
    @Transactional
    public void delete(Long id) {
        log.info("Removendo tipo de combustível id={}", id);
        findEntityById(id); // garante existência antes de tentar deletar

        if (fuelPumpRepository.existsByFuelTypeId(id)) {
            throw new BusinessException(
                    "Não é possível excluir o combustível pois existem bombas associadas a ele. " +
                    "Remova ou reatribua as bombas antes de excluir o combustível."
            );
        }

        fuelTypeRepository.deleteById(id);
        log.info("Tipo de combustível id={} removido", id);
    }

    // ── Helpers internos ───────────────────────────────────────────────────────

    /**
     * Busca entidade pelo ID ou lança {@link ResourceNotFoundException}.
     */
    public FuelType findEntityById(Long id) {
        return fuelTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, "id", id));
    }

    /**
     * Valida unicidade do nome; ignora o próprio registro no caso de updates.
     *
     * @param name nome a validar
     * @param id   ID a ignorar (null em criações)
     */
    private void validateUniqueName(String name, Long id) {
        boolean duplicado = (id == null)
                ? fuelTypeRepository.existsByName(name)
                : fuelTypeRepository.existsByNameAndIdNot(name, id);

        if (duplicado) {
            throw new ConflictException(
                    String.format("Já existe um tipo de combustível com o nome '%s'.", name));
        }
    }
}
