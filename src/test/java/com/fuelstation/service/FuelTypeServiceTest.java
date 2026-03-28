package com.fuelstation.service;

import com.fuelstation.exception.BusinessException;
import com.fuelstation.exception.ConflictException;
import com.fuelstation.exception.ResourceNotFoundException;
import com.fuelstation.mapper.FuelTypeMapper;
import com.fuelstation.model.dto.request.FuelTypeRequest;
import com.fuelstation.model.dto.response.FuelTypeResponse;
import com.fuelstation.model.entity.FuelType;
import com.fuelstation.repository.FuelPumpRepository;
import com.fuelstation.repository.FuelTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * Testes unitários para {@link FuelTypeService}.
 *
 * <p>Utiliza Mockito para isolar a camada de serviço dos repositórios e mappers.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FuelTypeService")
class FuelTypeServiceTest {

    @Mock
    private FuelTypeRepository fuelTypeRepository;

    @Mock
    private FuelPumpRepository fuelPumpRepository;

    @Mock
    private FuelTypeMapper fuelTypeMapper;

    @InjectMocks
    private FuelTypeService fuelTypeService;

    // ── Fixtures ───────────────────────────────────────────────────────────────

    private FuelType gasolina;
    private FuelTypeResponse gasolinaResponse;
    private FuelTypeRequest gasolinaRequest;

    @BeforeEach
    void setUp() {
        gasolina = FuelType.builder()
                .id(1L)
                .name("Gasolina Comum")
                .pricePerLiter(new BigDecimal("5.890"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        gasolinaResponse = new FuelTypeResponse();
        gasolinaResponse.setId(1L);
        gasolinaResponse.setName("Gasolina Comum");
        gasolinaResponse.setPricePerLiter(new BigDecimal("5.890"));

        gasolinaRequest = new FuelTypeRequest();
        gasolinaRequest.setName("Gasolina Comum");
        gasolinaRequest.setPricePerLiter(new BigDecimal("5.890"));
    }

    // ── findAll ────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        @Test
        @DisplayName("deve retornar lista de combustíveis com sucesso")
        void shouldReturnListSuccessfully() {
            given(fuelTypeRepository.findAll()).willReturn(List.of(gasolina));
            given(fuelTypeMapper.toResponseList(List.of(gasolina))).willReturn(List.of(gasolinaResponse));

            List<FuelTypeResponse> result = fuelTypeService.findAll();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Gasolina Comum");
            then(fuelTypeRepository).should().findAll();
        }

        @Test
        @DisplayName("deve retornar lista vazia quando não há combustíveis")
        void shouldReturnEmptyList() {
            given(fuelTypeRepository.findAll()).willReturn(List.of());
            given(fuelTypeMapper.toResponseList(List.of())).willReturn(List.of());

            assertThat(fuelTypeService.findAll()).isEmpty();
        }
    }

    // ── findById ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("deve retornar combustível pelo ID com sucesso")
        void shouldReturnFuelTypeById() {
            given(fuelTypeRepository.findById(1L)).willReturn(Optional.of(gasolina));
            given(fuelTypeMapper.toResponse(gasolina)).willReturn(gasolinaResponse);

            FuelTypeResponse result = fuelTypeService.findById(1L);

            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Gasolina Comum");
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException quando ID não existe")
        void shouldThrowWhenNotFound() {
            given(fuelTypeRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> fuelTypeService.findById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ── create ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("create()")
    class Create {

        @Test
        @DisplayName("deve criar combustível com sucesso")
        void shouldCreateSuccessfully() {
            given(fuelTypeRepository.existsByName("Gasolina Comum")).willReturn(false);
            given(fuelTypeMapper.toEntity(gasolinaRequest)).willReturn(gasolina);
            given(fuelTypeRepository.save(gasolina)).willReturn(gasolina);
            given(fuelTypeMapper.toResponse(gasolina)).willReturn(gasolinaResponse);

            FuelTypeResponse result = fuelTypeService.create(gasolinaRequest);

            assertThat(result.getName()).isEqualTo("Gasolina Comum");
            then(fuelTypeRepository).should().save(any(FuelType.class));
        }

        @Test
        @DisplayName("deve lançar ConflictException quando nome já existe")
        void shouldThrowConflictWhenNameExists() {
            given(fuelTypeRepository.existsByName("Gasolina Comum")).willReturn(true);

            assertThatThrownBy(() -> fuelTypeService.create(gasolinaRequest))
                    .isInstanceOf(ConflictException.class)
                    .hasMessageContaining("Gasolina Comum");

            then(fuelTypeRepository).should(never()).save(any());
        }
    }

    // ── update ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update()")
    class Update {

        @Test
        @DisplayName("deve atualizar combustível com sucesso")
        void shouldUpdateSuccessfully() {
            given(fuelTypeRepository.findById(1L)).willReturn(Optional.of(gasolina));
            given(fuelTypeRepository.existsByNameAndIdNot("Gasolina Comum", 1L)).willReturn(false);
            given(fuelTypeRepository.save(gasolina)).willReturn(gasolina);
            given(fuelTypeMapper.toResponse(gasolina)).willReturn(gasolinaResponse);

            FuelTypeResponse result = fuelTypeService.update(1L, gasolinaRequest);

            assertThat(result).isNotNull();
            then(fuelTypeMapper).should().updateEntityFromRequest(gasolinaRequest, gasolina);
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException ao atualizar ID inexistente")
        void shouldThrowWhenIdNotFound() {
            given(fuelTypeRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> fuelTypeService.update(99L, gasolinaRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ── delete ─────────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("delete()")
    class Delete {

        @Test
        @DisplayName("deve remover combustível com sucesso")
        void shouldDeleteSuccessfully() {
            given(fuelTypeRepository.findById(1L)).willReturn(Optional.of(gasolina));
            given(fuelPumpRepository.existsByFuelTypeId(1L)).willReturn(false);

            fuelTypeService.delete(1L);

            then(fuelTypeRepository).should().deleteById(1L);
        }

        @Test
        @DisplayName("deve lançar BusinessException quando há bombas associadas")
        void shouldThrowWhenPumpsAssociated() {
            given(fuelTypeRepository.findById(1L)).willReturn(Optional.of(gasolina));
            given(fuelPumpRepository.existsByFuelTypeId(1L)).willReturn(true);

            assertThatThrownBy(() -> fuelTypeService.delete(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("bombas associadas");

            then(fuelTypeRepository).should(never()).deleteById(any());
        }

        @Test
        @DisplayName("deve lançar ResourceNotFoundException ao deletar ID inexistente")
        void shouldThrowWhenNotFound() {
            given(fuelTypeRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> fuelTypeService.delete(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
