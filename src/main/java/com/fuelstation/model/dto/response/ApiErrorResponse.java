package com.fuelstation.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Resposta padronizada para erros da API.
 *
 * <p>Utilizada pelo {@code GlobalExceptionHandler} para retornar
 * mensagens de erro estruturadas e consistentes em todos os endpoints.</p>
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    /** Timestamp em que o erro ocorreu. */
    private LocalDateTime timestamp;

    /** Código HTTP do erro (ex: 400, 404, 409). */
    private int status;

    /** Texto do status HTTP (ex: "Bad Request"). */
    private String error;

    /** Mensagem descritiva do problema. */
    private String message;

    /** Path da requisição que originou o erro. */
    private String path;

    /**
     * Lista de erros de validação (campo + mensagem).
     * Presente apenas em erros 400 de validação de campos.
     */
    private List<FieldError> fieldErrors;

    /**
     * Detalhe de um erro de validação em campo específico.
     */
    @Data
    @Builder
    public static class FieldError {
        private String field;
        private Object rejectedValue;
        private String message;
    }
}
