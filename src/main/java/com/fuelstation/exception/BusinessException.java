package com.fuelstation.exception;

/**
 * Exceção para violações de regra de negócio.
 * Resulta em resposta HTTP 422 Unprocessable Entity.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
