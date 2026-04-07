package com.fuelstation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuelstation.exception.ResourceNotFoundException;
import com.fuelstation.model.dto.request.FuelingRequest;
import com.fuelstation.model.dto.response.FuelingResponse;
import com.fuelstation.model.dto.response.PageResponse;
import com.fuelstation.service.FuelingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de slice da camada web para {@link FuelingController}.
 */
@WebMvcTest(FuelingController.class)
@DisplayName("FuelingController")
class FuelingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private FuelingService fuelingService;

    private FuelingResponse fuelingResponse;
    private FuelingRequest fuelingRequest;

    private static final String BASE_URL = "/api/v1/fuelings";

    @BeforeEach
    void setUp() {
        fuelingResponse = new FuelingResponse();
        fuelingResponse.setId(1L);
        fuelingResponse.setFuelingDate(LocalDate.of(2025, 1, 10));
        fuelingResponse.setLiters(new BigDecimal("40.000"));
        fuelingResponse.setTotalValue(new BigDecimal("235.60"));

        fuelingRequest = new FuelingRequest();
        fuelingRequest.setPumpId(1L);
        fuelingRequest.setFuelTypeId(1L);
        fuelingRequest.setFuelingDate(LocalDate.of(2025, 1, 10));
        fuelingRequest.setLiters(new BigDecimal("40.000"));
        fuelingRequest.setTotalValue(new BigDecimal("235.60"));
    }

    @Nested
    @DisplayName("GET /api/v1/fuelings")
    class GetAll {

        @Test
        @DisplayName("deve retornar 200 com lista de abastecimentos")
        void shouldReturn200() throws Exception {
            given(fuelingService.findAll(any(Pageable.class)))
                    .willReturn(new PageResponse<>(List.of(fuelingResponse), 0, 20, 1, 1, true, true));

            mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].liters").value(40.000));
        }

        @Test
        @DisplayName("deve usar filtros quando parâmetros são fornecidos")
        void shouldUseFiltersWhenParamsProvided() throws Exception {
            given(fuelingService.findWithFilters(any(), any(), any(), any(Pageable.class)))
                    .willReturn(new PageResponse<>(List.of(fuelingResponse), 0, 20, 1, 1, true, true));

            mockMvc.perform(get(BASE_URL)
                            .param("pumpId", "1")
                            .param("startDate", "2025-01-01")
                            .param("endDate", "2025-01-31")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1));

            then(fuelingService).should().findWithFilters(any(), any(), any(), any(Pageable.class));
            then(fuelingService).should(never()).findAll(any(Pageable.class));
        }

        @Test
        @DisplayName("deve repassar paginação para listagem sem filtros")
        void shouldForwardPaginationWithoutFilters() throws Exception {
            given(fuelingService.findAll(any(Pageable.class)))
                    .willReturn(new PageResponse<>(List.of(fuelingResponse), 1, 3, 7, 3, false, false));

            mockMvc.perform(get(BASE_URL)
                            .param("page", "1")
                            .param("size", "3")
                            .param("sort", "fuelingDate,asc")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(1))
                    .andExpect(jsonPath("$.size").value(3));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            then(fuelingService).should().findAll(pageableCaptor.capture());

            Pageable pageable = pageableCaptor.getValue();
            assertThat(pageable.getPageNumber()).isEqualTo(1);
            assertThat(pageable.getPageSize()).isEqualTo(3);
            assertThat(pageable.getSort().getOrderFor("fuelingDate")).isNotNull();
            assertThat(pageable.getSort().getOrderFor("fuelingDate").getDirection()).isEqualTo(Sort.Direction.ASC);
        }

        @Test
        @DisplayName("deve repassar paginação para listagem com filtros")
        void shouldForwardPaginationWithFilters() throws Exception {
            given(fuelingService.findWithFilters(any(), any(), any(), any(Pageable.class)))
                    .willReturn(new PageResponse<>(List.of(fuelingResponse), 0, 2, 1, 1, true, true));

            mockMvc.perform(get(BASE_URL)
                            .param("pumpId", "1")
                            .param("startDate", "2025-01-01")
                            .param("endDate", "2025-01-31")
                            .param("page", "0")
                            .param("size", "2")
                            .param("sort", "fuelingDate,desc")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.size").value(2));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            then(fuelingService).should().findWithFilters(any(), any(), any(), pageableCaptor.capture());

            Pageable pageable = pageableCaptor.getValue();
            assertThat(pageable.getPageNumber()).isEqualTo(0);
            assertThat(pageable.getPageSize()).isEqualTo(2);
            assertThat(pageable.getSort().getOrderFor("fuelingDate")).isNotNull();
            assertThat(pageable.getSort().getOrderFor("fuelingDate").getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/fuelings")
    class Create {

        @Test
        @DisplayName("deve retornar 201 ao registrar abastecimento válido")
        void shouldReturn201() throws Exception {
            given(fuelingService.create(any(FuelingRequest.class))).willReturn(fuelingResponse);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(fuelingRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("deve retornar 400 quando litragem é zero")
        void shouldReturn400WhenLitersZero() throws Exception {
            fuelingRequest.setLiters(BigDecimal.ZERO);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(fuelingRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 404 quando bomba não existe")
        void shouldReturn404WhenPumpNotFound() throws Exception {
            given(fuelingService.create(any(FuelingRequest.class)))
                    .willThrow(new ResourceNotFoundException("Bomba de Combustível", "id", 1L));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(fuelingRequest)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/v1/fuelings/{id}")
    class Delete {

        @Test
        @DisplayName("deve retornar 204 ao remover abastecimento")
        void shouldReturn204() throws Exception {
            willDoNothing().given(fuelingService).delete(1L);

            mockMvc.perform(delete(BASE_URL + "/1"))
                    .andExpect(status().isNoContent());
        }
    }
}
