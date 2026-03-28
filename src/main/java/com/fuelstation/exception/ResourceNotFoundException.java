package com.fuelstation.exception;

/**
 * Exceção lançada quando um recurso solicitado não é encontrado no banco de dados.
 * Resulta em resposta HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param resourceName nome da entidade (ex: "FuelType")
     * @param fieldName    campo usado na busca (ex: "id")
     * @param fieldValue   valor do campo que não foi encontrado
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s não encontrado(a) com %s: '%s'", resourceName, fieldName, fieldValue));
    }
}
