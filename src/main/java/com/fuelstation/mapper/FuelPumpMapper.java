package com.fuelstation.mapper;

import com.fuelstation.model.dto.request.FuelPumpRequest;
import com.fuelstation.model.dto.response.FuelPumpResponse;
import com.fuelstation.model.entity.FuelPump;
import com.fuelstation.model.entity.FuelType;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper MapStruct para conversão entre {@link FuelPump} e seus DTOs.
 *
 * <p>Uma bomba pode ter múltiplos tipos de combustível associados (ManyToMany).</p>
 */
@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FuelPumpMapper {

    /**
     * Converte entidade para DTO de resposta.
     * Mapeia Set<FuelType> para List<FuelTypeSummary> usando método auxiliar.
     */
    @Mapping(target = "fuelTypes", source = "fuelTypes")
    FuelPumpResponse toResponse(FuelPump fuelPump);

    List<FuelPumpResponse> toResponseList(List<FuelPump> fuelPumps);

    /**
     * Converte DTO de criação em entidade parcial.
     * Os combustíveis (fuelTypes) são resolvidos pelo service (busca no repositório).
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fuelTypes", ignore = true)
    @Mapping(target = "fuelings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FuelPump toEntity(FuelPumpRequest request);

    /**
     * Atualiza nome de uma bomba existente.
     * fuelTypes são atualizados manualmente no service para controle de integridade.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fuelTypes", ignore = true)
    @Mapping(target = "fuelings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(FuelPumpRequest request, @MappingTarget FuelPump target);

    /**
     * Método auxiliar: converte Set<FuelType> para List<FuelTypeSummary>.
     * Utilizado automaticamente pelo MapStruct no mapeamento de toResponse.
     */
    default List<FuelPumpResponse.FuelTypeSummary> mapFuelTypesToSummaries(Set<FuelType> fuelTypes) {
        if (fuelTypes == null || fuelTypes.isEmpty()) {
            return List.of();
        }
        return fuelTypes.stream()
                .map(this::toFuelTypeSummary)
                .collect(Collectors.toList());
    }

    /**
     * Converte um FuelType para seu resumo (FuelTypeSummary).
     */
    default FuelPumpResponse.FuelTypeSummary toFuelTypeSummary(FuelType fuelType) {
        if (fuelType == null) return null;
        FuelPumpResponse.FuelTypeSummary summary = new FuelPumpResponse.FuelTypeSummary();
        summary.setId(fuelType.getId());
        summary.setName(fuelType.getName());
        summary.setPricePerLiter(fuelType.getPricePerLiter());
        return summary;
    }
}


