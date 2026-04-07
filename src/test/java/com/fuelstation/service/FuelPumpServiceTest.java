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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários para {@link FuelPumpService}.
 * Testa a lógica de bombas com múltiplos tipos de combustível (ManyToMany).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FuelPumpService")
class FuelPumpServiceTest {

    @Mock private FuelPumpRepository fuelPumpRepository;
    @Mock private FuelingRepository fuelingRepository;
    @Mock private FuelTypeService fuelTypeService;
    @Mock private FuelPumpMapper fuelPumpMapper;

    @InjectMocks
    private FuelPumpService fuelPumpService;

    private FuelType gasolina;
    private FuelType etanol;
    private FuelPump bomba1;
    private FuelPumpResponse bomba1Response;
    private FuelPumpRequest bomba1Request;

    @BeforeEach
    void setUp() {
        gasolina = FuelType.builder()
                .id(1L).name("Gasolina Comum")
                .pricePerLiter(new BigDecimal("5.890"))
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        etanol = FuelType.builder()
                .id(2L).name("Etanol")
                .pricePerLiter(new BigDecimal("4.290"))
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        // Bomba com múltiplos combustíveis
        Set<FuelType> fuelTypes = new HashSet<>();
        fuelTypes.add(gasolina);
        fuelTypes.add(etanol);

        bomba1 = FuelPump.builder()
                .id(1L).name("Bomba 01")
                .fuelTypes(fuelTypes)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        bomba1Response = new FuelPumpResponse();
        bomba1Response.setId(1L);
        bomba1Response.setName("Bomba 01");

        // Request com múltiplos combustíveis
        bomba1Request = new FuelPumpRequest();
        bomba1Request.setName("Bomba 01");
        bomba1Request.setFuelTypeIds(Set.of(1L, 2L));
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("deve criar bomba com múltiplos combustíveis")
        void shouldCreateWithMultipleFuels() {
            given(fuelPumpRepository.existsByName("Bomba 01")).willReturn(false);
            given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);
            given(fuelTypeService.findEntityById(2L)).willReturn(etanol);
            given(fuelPumpMapper.toEntity(bomba1Request)).willReturn(bomba1);
            given(fuelPumpRepository.save(bomba1)).willReturn(bomba1);
            given(fuelPumpMapper.toResponse(bomba1)).willReturn(bomba1Response);

            FuelPumpResponse result = fuelPumpService.create(bomba1Request);

            assertThat(result.getName()).isEqualTo("Bomba 01");
            then(fuelTypeService).should(times(2)).findEntityById(any());
            then(fuelPumpRepository).should().save(any(FuelPump.class));
        }

        @Test
        @DisplayName("deve lançar BusinessException quando fuelTypeIds está vazio")
        void shouldThrowWhenNoFuelTypes() {
            FuelPumpRequest request = new FuelPumpRequest();
            request.setName("Bomba Inválida");
            request.setFuelTypeIds(Set.of());

            assertThatThrownBy(() -> fuelPumpService.create(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("pelo menos um tipo de combustível");

            then(fuelPumpRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ConflictException quando nome já existe")
        void shouldThrowWhenNameExists() {
            given(fuelPumpRepository.existsByName("Bomba 01")).willReturn(true);

            assertThatThrownBy(() -> fuelPumpService.create(bomba1Request))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Bomba 01");

            then(fuelPumpRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando combustível não existe")
        void shouldThrowWhenFuelTypeNotFound() {
            given(fuelPumpRepository.existsByName("Bomba 01")).willReturn(false);
            // lenient: Set iteration order is non-deterministic, so this stub may or may not be called
            lenient().doReturn(gasolina).when(fuelTypeService).findEntityById(1L);
            given(fuelTypeService.findEntityById(2L))
                    .willThrow(new ResourceNotFoundException("Tipo de Combustível", "id", 2L));

            assertThatThrownBy(() -> fuelPumpService.create(bomba1Request))
                    .isInstanceOf(ResourceNotFoundException.class);

            then(fuelPumpRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("deve adicionar novo combustível mantendo os existentes (merge)")
        void shouldMergeFuelTypes() {
            // Bomba tem [gasolina, etanol], request pede adicionar [gasolina] (já existe)
            FuelPumpRequest updateRequest = new FuelPumpRequest();
            updateRequest.setName("Bomba 01");
            updateRequest.setFuelTypeIds(Set.of(1L));

            given(fuelPumpRepository.findById(1L)).willReturn(Optional.of(bomba1));
            given(fuelPumpRepository.existsByNameAndIdNot("Bomba 01", 1L)).willReturn(false);
            given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);
            given(fuelPumpRepository.save(bomba1)).willReturn(bomba1);
            given(fuelPumpMapper.toResponse(bomba1)).willReturn(bomba1Response);

            FuelPumpResponse result = fuelPumpService.update(1L, updateRequest);

            assertThat(result.getName()).isEqualTo("Bomba 01");
            // Deve manter os existentes e adicionar os novos (sem duplicatas)
            assertThat(bomba1.getFuelTypes()).containsExactlyInAnyOrder(gasolina, etanol);
        }

        @Test
        @DisplayName("deve adicionar tipo de combustível novo sem remover existentes")
        void shouldAddNewFuelTypeWithoutRemovingExisting() {
            // Bomba tem [gasolina, etanol], request pede adicionar diesel (novo)
            FuelType diesel = FuelType.builder()
                    .id(4L).name("Diesel S10")
                    .pricePerLiter(new BigDecimal("6.490"))
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                    .build();

            FuelPumpRequest updateRequest = new FuelPumpRequest();
            updateRequest.setName("Bomba 01");
            updateRequest.setFuelTypeIds(Set.of(4L));

            given(fuelPumpRepository.findById(1L)).willReturn(Optional.of(bomba1));
            given(fuelPumpRepository.existsByNameAndIdNot("Bomba 01", 1L)).willReturn(false);
            given(fuelTypeService.findEntityById(4L)).willReturn(diesel);
            given(fuelPumpRepository.save(bomba1)).willReturn(bomba1);
            given(fuelPumpMapper.toResponse(bomba1)).willReturn(bomba1Response);

            fuelPumpService.update(1L, updateRequest);

            // Deve ter os 3: gasolina, etanol (existentes) + diesel (novo)
            assertThat(bomba1.getFuelTypes()).containsExactlyInAnyOrder(gasolina, etanol, diesel);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando fuelTypeIds fica vazio na atualização")
        void shouldThrowWhenNoFuelTypesInUpdate() {
            FuelPumpRequest updateRequest = new FuelPumpRequest();
            updateRequest.setName("Bomba 01");
            updateRequest.setFuelTypeIds(Set.of());

            given(fuelPumpRepository.findById(1L)).willReturn(Optional.of(bomba1));

            assertThatThrownBy(() -> fuelPumpService.update(1L, updateRequest))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("pelo menos um tipo de combustível");

            then(fuelPumpRepository).should().findById(1L);
            then(fuelPumpRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("findAll(Pageable)")
    class FindAll {

        @Test
        @DisplayName("deve retornar todas as bombas com combustíveis")
        void shouldReturnAllPumps() {
            Pageable pageable = PageRequest.of(0, 20);
            given(fuelPumpRepository.findPageIds(pageable)).willReturn(new PageImpl<>(List.of(1L), pageable, 1));
            given(fuelPumpRepository.findAllByIdInWithFuelTypes(List.of(1L))).willReturn(List.of(bomba1));
            given(fuelPumpMapper.toResponse(bomba1)).willReturn(bomba1Response);

            PageResponse<FuelPumpResponse> result = fuelPumpService.findAll(pageable);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).getName()).isEqualTo("Bomba 01");
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve remover bomba sem abastecimentos")
        void shouldDeleteWithNoFuelings() {
            given(fuelPumpRepository.findById(1L)).willReturn(Optional.of(bomba1));
            given(fuelingRepository.existsByPumpId(1L)).willReturn(false);

            fuelPumpService.delete(1L);

            then(fuelPumpRepository).should().deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando há abastecimentos vinculados")
        void shouldThrowWhenHasFuelings() {
            given(fuelPumpRepository.findById(1L)).willReturn(Optional.of(bomba1));
            given(fuelingRepository.existsByPumpId(1L)).willReturn(true);

            assertThatThrownBy(() -> fuelPumpService.delete(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("abastecimentos registrados");

            then(fuelPumpRepository).should(never()).deleteById(any());
        }
    }
}
