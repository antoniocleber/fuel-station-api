package com.fuelstation.repository;

import com.fuelstation.model.entity.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório JPA para a entidade {@link FuelType}.
 *
 * <p>Spring Data JPA gera automaticamente as implementações
 * de CRUD e das consultas derivadas de nomes de métodos.</p>
 */
@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {

    /**
     * Verifica se já existe um combustível com o nome informado.
     *
     * @param name nome a verificar (case-sensitive)
     * @return {@code true} se existir
     */
    boolean existsByName(String name);

    /**
     * Verifica duplicata de nome excluindo o próprio registro (para updates).
     *
     * @param name nome a verificar
     * @param id   ID do registro a ignorar
     * @return {@code true} se outro registro usar o mesmo nome
     */
    boolean existsByNameAndIdNot(String name, Long id);

    Optional<FuelType> findByName(String name);
}
