package com.fuelstation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuelstation.exception.ConflictException;
import com.fuelstation.exception.ResourceNotFoundException;
import com.fuelstation.model.dto.request.FuelTypeRequest;
import com.fuelstation.model.dto.response.FuelTypeResponse;
import com.fuelstation.model.dto.response.PageResponse;
import com.fuelstation.service.FuelTypeService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de slice da camada web para {@link FuelTypeController}.
 *
 * <p>Usa {@code @WebMvcTest} para carregar apenas o contexto web (sem banco de dados),
 * isolando o controller do service via mock.</p>
 */
@WebMvcTest(FuelTypeController.class)
@DisplayName("FuelTypeController")
class FuelTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FuelTypeService fuelTypeService;

    private FuelTypeResponse gasolinaResponse;
    private FuelTypeRequest gasolinaRequest;

    private static final String BASE_URL = "/api/v1/fuel-types";

    @BeforeEach
    void setUp() {
        gasolinaResponse = new FuelTypeResponse();
        gasolinaResponse.setId(1L);
        gasolinaResponse.setName("Gasolina Comum");
        gasolinaResponse.setPricePerLiter(new BigDecimal("5.890"));
        gasolinaResponse.setCreatedAt(LocalDateTime.now());
        gasolinaResponse.setUpdatedAt(LocalDateTime.now());

        gasolinaRequest = new FuelTypeRequest();
        gasolinaRequest.setName("Gasolina Comum");
        gasolinaRequest.setPricePerLiter(new BigDecimal("5.890"));
    }

    // ── GET /api/v1/fuel-types ────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/fuel-types")
    class GetAll {

        @Test
        @DisplayName("deve retornar 200 com lista de combustíveis")
        void shouldReturn200WithList() throws Exception {
            given(fuelTypeService.findAll(any(Pageable.class)))
                    .willReturn(new PageResponse<>(List.of(gasolinaResponse), 0, 20, 1, 1, true, true));

            mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(1))
                    .andExpect(jsonPath("$.content[0].name").value("Gasolina Comum"))
                    .andExpect(jsonPath("$.content[0].pricePerLiter").value(5.890))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("deve retornar 200 com lista vazia")
        void shouldReturn200WithEmptyList() throws Exception {
            given(fuelTypeService.findAll(any(Pageable.class)))
                    .willReturn(new PageResponse<>(List.of(), 0, 20, 0, 0, true, true));

            mockMvc.perform(get(BASE_URL).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(0));
        }

        @Test
        @DisplayName("deve repassar page, size e sort para o service")
        void shouldForwardPaginationParams() throws Exception {
            given(fuelTypeService.findAll(any(Pageable.class)))
                    .willReturn(new PageResponse<>(List.of(gasolinaResponse), 2, 5, 11, 3, false, true));

            mockMvc.perform(get(BASE_URL)
                            .param("page", "2")
                            .param("size", "5")
                            .param("sort", "name,desc")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.page").value(2))
                    .andExpect(jsonPath("$.size").value(5));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            then(fuelTypeService).should().findAll(pageableCaptor.capture());

            Pageable pageable = pageableCaptor.getValue();
            assertThat(pageable.getPageNumber()).isEqualTo(2);
            assertThat(pageable.getPageSize()).isEqualTo(5);
            assertThat(pageable.getSort().getOrderFor("name")).isNotNull();
            assertThat(pageable.getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.DESC);
        }
    }

    // ── GET /api/v1/fuel-types/{id} ───────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/v1/fuel-types/{id}")
    class GetById {

        @Test
        @DisplayName("deve retornar 200 quando combustível existe")
        void shouldReturn200WhenFound() throws Exception {
            given(fuelTypeService.findById(1L)).willReturn(gasolinaResponse);

            mockMvc.perform(get(BASE_URL + "/1").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Gasolina Comum"));
        }

        @Test
        @DisplayName("deve retornar 404 quando combustível não existe")
        void shouldReturn404WhenNotFound() throws Exception {
            given(fuelTypeService.findById(99L))
                    .willThrow(new ResourceNotFoundException("Tipo de Combustível", "id", 99L));

            mockMvc.perform(get(BASE_URL + "/99").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    // ── POST /api/v1/fuel-types ───────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/v1/fuel-types")
    class Create {

        @Test
        @DisplayName("deve retornar 201 ao criar combustível válido")
        void shouldReturn201WhenCreated() throws Exception {
            given(fuelTypeService.create(any(FuelTypeRequest.class))).willReturn(gasolinaResponse);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(gasolinaRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Gasolina Comum"));
        }

        @Test
        @DisplayName("deve retornar 400 quando nome está em branco")
        void shouldReturn400WhenNameBlank() throws Exception {
            gasolinaRequest.setName("");

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(gasolinaRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.fieldErrors").isArray());
        }

        @Test
        @DisplayName("deve retornar 400 quando preço é nulo")
        void shouldReturn400WhenPriceNull() throws Exception {
            gasolinaRequest.setPricePerLiter(null);

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(gasolinaRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("deve retornar 409 quando nome já existe")
        void shouldReturn409WhenNameConflict() throws Exception {
            given(fuelTypeService.create(any(FuelTypeRequest.class)))
                    .willThrow(new ConflictException("Já existe um tipo de combustível com o nome 'Gasolina Comum'."));

            mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(gasolinaRequest)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    // ── PUT /api/v1/fuel-types/{id} ───────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/v1/fuel-types/{id}")
    class Update {

        @Test
        @DisplayName("deve retornar 200 ao atualizar combustível")
        void shouldReturn200WhenUpdated() throws Exception {
            given(fuelTypeService.update(eq(1L), any(FuelTypeRequest.class))).willReturn(gasolinaResponse);

            mockMvc.perform(put(BASE_URL + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(gasolinaRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Gasolina Comum"));
        }
    }

    // ── DELETE /api/v1/fuel-types/{id} ────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/v1/fuel-types/{id}")
    class DeleteTest {

        @Test
        @DisplayName("deve retornar 204 ao deletar combustível existente")
        void shouldReturn204WhenDeleted() throws Exception {
            willDoNothing().given(fuelTypeService).delete(1L);

            mockMvc.perform(delete(BASE_URL + "/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("deve retornar 404 ao deletar combustível inexistente")
        void shouldReturn404WhenNotFound() throws Exception {
            willThrow(new ResourceNotFoundException("Tipo de Combustível", "id", 99L))
                    .given(fuelTypeService).delete(99L);

            mockMvc.perform(delete(BASE_URL + "/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
