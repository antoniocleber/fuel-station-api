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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários para {@link FuelingService}.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FuelingService")
class FuelingServiceTest {

    @Mock private FuelingRepository fuelingRepository;
    @Mock private FuelPumpService fuelPumpService;
    @Mock private FuelTypeService fuelTypeService;
    @Mock private FuelingMapper fuelingMapper;

    @InjectMocks
    private FuelingService fuelingService;

    private FuelType gasolina;
    private FuelPump bomba1;
    private Fueling fueling1;
    private FuelingResponse fuelingResponse1;
    private FuelingRequest fuelingRequest;

    @BeforeEach
    void setUp() {
        gasolina = FuelType.builder()
                .id(1L).name("Gasolina Comum")
                .pricePerLiter(new BigDecimal("5.890"))
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        // Bomba com múltiplos combustíveis (ManyToMany)
        Set<FuelType> fuelTypes = Set.of(gasolina);
        bomba1 = FuelPump.builder()
                .id(1L).name("Bomba 01")
                .fuelTypes(fuelTypes)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        fueling1 = Fueling.builder()
                .id(1L)
                .pump(bomba1)
                .fuelType(gasolina)
                .fuelingDate(LocalDate.of(2025, 1, 10))
                .liters(new BigDecimal("40.000"))
                .totalValue(new BigDecimal("235.60"))
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        fuelingResponse1 = new FuelingResponse();
        fuelingResponse1.setId(1L);
        fuelingResponse1.setLiters(new BigDecimal("40.000"));
        fuelingResponse1.setTotalValue(new BigDecimal("235.60"));

        fuelingRequest = new FuelingRequest();
        fuelingRequest.setPumpId(1L);
        fuelingRequest.setFuelTypeId(1L);
        fuelingRequest.setFuelingDate(LocalDate.of(2025, 1, 10));
        fuelingRequest.setLiters(new BigDecimal("40.000"));
        fuelingRequest.setTotalValue(new BigDecimal("235.60"));
    }

    @Nested
    @DisplayName("findAll(Pageable)")
    class FindAll {

        @Test
        @DisplayName("deve retornar lista de abastecimentos")
        void shouldReturnList() {
            Pageable pageable = PageRequest.of(0, 20);
            given(fuelingRepository.findPageIds(pageable)).willReturn(new PageImpl<>(List.of(1L), pageable, 1));
            given(fuelingRepository.findAllByIdInWithDetails(List.of(1L))).willReturn(List.of(fueling1));
            given(fuelingMapper.toResponse(fueling1)).willReturn(fuelingResponse1);

            PageResponse<FuelingResponse> result = fuelingService.findAll(pageable);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).getLiters()).isEqualByComparingTo("40.000");
        }
    }

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("deve retornar abastecimento por ID")
        void shouldReturnById() {
            given(fuelingRepository.findByIdWithDetails(1L)).willReturn(Optional.of(fueling1));
            given(fuelingMapper.toResponse(fueling1)).willReturn(fuelingResponse1);

            FuelingResponse result = fuelingService.findById(1L);

            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando ID não existe")
        void shouldThrowWhenNotFound() {
            given(fuelingRepository.findByIdWithDetails(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> fuelingService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("deve criar abastecimento com liters e totalValue informados")
        void shouldCreateSuccessfully() {
            given(fuelPumpService.findEntityById(1L)).willReturn(bomba1);
            given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);
            given(fuelingMapper.toEntity(fuelingRequest)).willReturn(fueling1);
            given(fuelingRepository.save(fueling1)).willReturn(fueling1);
            given(fuelingRepository.findByIdWithDetails(1L)).willReturn(Optional.of(fueling1));
            given(fuelingMapper.toResponse(fueling1)).willReturn(fuelingResponse1);

            FuelingResponse result = fuelingService.create(fuelingRequest);

            assertThat(result).isNotNull();
            then(fuelingRepository).should().save(any(Fueling.class));
        }

        @Test
        @DisplayName("deve calcular totalValue quando apenas liters informado")
        void shouldCalculateTotalValueFromLiters() {
            FuelingRequest req = new FuelingRequest();
            req.setPumpId(1L);
            req.setFuelTypeId(1L);
            req.setFuelingDate(LocalDate.of(2025, 1, 10));
            req.setLiters(new BigDecimal("40.000"));
            req.setTotalValue(null); // apenas liters

            given(fuelPumpService.findEntityById(1L)).willReturn(bomba1);
            given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);
            given(fuelingMapper.toEntity(any(FuelingRequest.class))).willReturn(fueling1);
            given(fuelingRepository.save(fueling1)).willReturn(fueling1);
            given(fuelingRepository.findByIdWithDetails(1L)).willReturn(Optional.of(fueling1));
            given(fuelingMapper.toResponse(fueling1)).willReturn(fuelingResponse1);

            fuelingService.create(req);

            // 40.000 × 5.890 = 235.60
            assertThat(req.getTotalValue()).isEqualByComparingTo("235.60");
        }

        @Test
        @DisplayName("deve calcular liters quando apenas totalValue informado")
        void shouldCalculateLitersFromTotalValue() {
            FuelingRequest req = new FuelingRequest();
            req.setPumpId(1L);
            req.setFuelTypeId(1L);
            req.setFuelingDate(LocalDate.of(2025, 1, 10));
            req.setLiters(null); // apenas totalValue
            req.setTotalValue(new BigDecimal("235.60"));

            given(fuelPumpService.findEntityById(1L)).willReturn(bomba1);
            given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);
            given(fuelingMapper.toEntity(any(FuelingRequest.class))).willReturn(fueling1);
            given(fuelingRepository.save(fueling1)).willReturn(fueling1);
            given(fuelingRepository.findByIdWithDetails(1L)).willReturn(Optional.of(fueling1));
            given(fuelingMapper.toResponse(fueling1)).willReturn(fuelingResponse1);

            fuelingService.create(req);

            // 235.60 / 5.890 = 40.000 (3 decimais)
            assertThat(req.getLiters()).isEqualByComparingTo("40.000");
        }

        @Test
        @DisplayName("deve lançar BusinessException quando liters e totalValue não informados")
        void shouldThrowWhenNeitherLitersNorTotalValue() {
            FuelingRequest req = new FuelingRequest();
            req.setPumpId(1L);
            req.setFuelTypeId(1L);
            req.setFuelingDate(LocalDate.of(2025, 1, 10));
            req.setLiters(null);
            req.setTotalValue(null);

            given(fuelPumpService.findEntityById(1L)).willReturn(bomba1);
            given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);

            assertThatThrownBy(() -> fuelingService.create(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("litros ou o valor total");

            then(fuelingRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("deve lançar BusinessException quando fuelType não pertence à bomba")
        void shouldThrowWhenFuelTypeNotInPump() {
            FuelType diesel = FuelType.builder()
                    .id(4L).name("Diesel S10")
                    .pricePerLiter(new BigDecimal("6.490"))
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                    .build();

            FuelingRequest req = new FuelingRequest();
            req.setPumpId(1L);
            req.setFuelTypeId(4L);
            req.setFuelingDate(LocalDate.of(2025, 1, 10));
            req.setLiters(new BigDecimal("40.000"));

            given(fuelPumpService.findEntityById(1L)).willReturn(bomba1);
            given(fuelTypeService.findEntityById(4L)).willReturn(diesel);

            assertThatThrownBy(() -> fuelingService.create(req))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("não está associado à bomba");

            then(fuelingRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando bomba não existe")
        void shouldThrowWhenPumpNotFound() {
            given(fuelPumpService.findEntityById(1L))
                    .willThrow(new ResourceNotFoundException("Bomba de Combustível", "id", 1L));

            assertThatThrownBy(() -> fuelingService.create(fuelingRequest))
                    .isInstanceOf(ResourceNotFoundException.class);

            then(fuelingRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve remover abastecimento com sucesso")
        void shouldDeleteSuccessfully() {
            given(fuelingRepository.existsById(1L)).willReturn(true);

            fuelingService.delete(1L);

            then(fuelingRepository).should().deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando ID não existe")
        void shouldThrowWhenNotFound() {
            given(fuelingRepository.existsById(99L)).willReturn(false);

            assertThatThrownBy(() -> fuelingService.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findWithFilters(..., Pageable)")
    class FindWithFilters {

        @Test
        @DisplayName("deve filtrar por bomba e período")
        void shouldFilterByPumpAndPeriod() {
            LocalDate start = LocalDate.of(2025, 1, 1);
            LocalDate end = LocalDate.of(2025, 1, 31);
            Pageable pageable = PageRequest.of(0, 20);
            given(fuelingRepository.findPageIdsWithFilters(1L, start, end, pageable))
                    .willReturn(new PageImpl<>(List.of(1L), pageable, 1));
            given(fuelingRepository.findAllByIdInWithDetails(List.of(1L))).willReturn(List.of(fueling1));
            given(fuelingMapper.toResponse(fueling1)).willReturn(fuelingResponse1);

            PageResponse<FuelingResponse> result = fuelingService.findWithFilters(1L, start, end, pageable);

            assertThat(result.content()).hasSize(1);
        }
    }
}
