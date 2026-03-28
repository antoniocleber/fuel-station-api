package com.fuelstation.mapper;

import com.fuelstation.model.dto.request.FuelingRequest;
import com.fuelstation.model.dto.response.FuelingResponse;
import com.fuelstation.model.entity.Fueling;
import com.fuelstation.model.entity.FuelType;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper MapStruct para conversão entre {@link Fueling} e seus DTOs.
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FuelingMapper {

    /**
     * Converte entidade para DTO de resposta.
     * Navega pelos relacionamentos pump → fuelTypes (ManyToMany) para montar o objeto aninhado.
     */
    @Mapping(target = "pump.id", source = "pump.id")
    @Mapping(target = "pump.name", source = "pump.name")
    @Mapping(target = "pump.fuelTypes", source = "pump.fuelTypes")
    FuelingResponse toResponse(Fueling fueling);

    List<FuelingResponse> toResponseList(List<Fueling> fuelings);

    /**
     * Converte DTO de criação em entidade parcial.
     * O pump é resolvido pelo service a partir do pumpId.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pump", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Fueling toEntity(FuelingRequest request);

    /**
     * Atualiza campos de um abastecimento existente.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pump", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(FuelingRequest request, @MappingTarget Fueling target);

    /**
     * Método auxiliar: converte Set<FuelType> para List<FuelTypeSummary>.
     */
    default List<FuelingResponse.FuelTypeSummary> mapFuelTypesToSummaries(Set<FuelType> fuelTypes) {
        if (fuelTypes == null || fuelTypes.isEmpty()) {
            return List.of();
        }
        return fuelTypes.stream()
                .map(this::toFuelTypeSummary)
                .collect(Collectors.toList());
    }

    /**
     * Converte um FuelType para seu resumo.
     */
    default FuelingResponse.FuelTypeSummary toFuelTypeSummary(FuelType fuelType) {
        if (fuelType == null) return null;
        FuelingResponse.FuelTypeSummary summary = new FuelingResponse.FuelTypeSummary();
        summary.setId(fuelType.getId());
        summary.setName(fuelType.getName());
        summary.setPricePerLiter(fuelType.getPricePerLiter());
        return summary;
    }
}
