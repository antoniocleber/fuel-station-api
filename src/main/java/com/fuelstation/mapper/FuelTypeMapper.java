package com.fuelstation.mapper;

import com.fuelstation.model.dto.request.FuelTypeRequest;
import com.fuelstation.model.dto.response.FuelTypeResponse;
import com.fuelstation.model.entity.FuelType;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper MapStruct para conversão entre {@link FuelType} e seus DTOs.
 *
 * <p>Gerado em tempo de compilação — sem reflection em runtime,
 * garantindo performance e type-safety.</p>
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FuelTypeMapper {

    /**
     * Converte entidade para DTO de resposta.
     *
     * @param fuelType entidade de origem
     * @return DTO de saída
     */
    FuelTypeResponse toResponse(FuelType fuelType);

    /**
     * Converte lista de entidades para lista de DTOs.
     *
     * @param fuelTypes lista de entidades
     * @return lista de DTOs
     */
    List<FuelTypeResponse> toResponseList(List<FuelType> fuelTypes);

    /**
     * Converte DTO de criação em entidade.
     * Os campos id, createdAt e updatedAt são ignorados (gerados automaticamente).
     *
     * @param request DTO de entrada
     * @return entidade pronta para persistência
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pumps", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FuelType toEntity(FuelTypeRequest request);

    /**
     * Atualiza campos de uma entidade existente a partir de um DTO.
     * Campos nulos no DTO são ignorados (PATCH-like behavior).
     *
     * @param request DTO com os dados novos
     * @param target  entidade a ser atualizada (modificada in-place)
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pumps", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(FuelTypeRequest request, @MappingTarget FuelType target);
}
