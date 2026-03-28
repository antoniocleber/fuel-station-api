package com.fuelstation.exception;

/**
 * Exceção para tentativas de criação de recursos duplicados.
 * Resulta em resposta HTTP 409 Conflict.
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
