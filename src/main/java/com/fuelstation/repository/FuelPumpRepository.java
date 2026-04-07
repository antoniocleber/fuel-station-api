package com.fuelstation.repository;

import com.fuelstation.model.entity.FuelPump;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Repositório JPA para a entidade {@link FuelPump}.
 */
@Repository
public interface FuelPumpRepository extends JpaRepository<FuelPump, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    /**
     * Verifica se alguma bomba está associada ao tipo de combustível informado (ManyToMany).
     * Usado para impedir a exclusão de combustíveis em uso.
     *
     * @param fuelTypeId ID do tipo de combustível
     * @return {@code true} se houver ao menos uma bomba associada
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM FuelPump p " +
           "JOIN p.fuelTypes ft WHERE ft.id = :fuelTypeId")
    boolean existsByFuelTypeId(@Param("fuelTypeId") Long fuelTypeId);

    /**
     * Busca todas as bombas que utilizam um determinado tipo de combustível
     * com carregamento eager dos tipos de combustível (evita N+1 query).
     *
     * @param fuelTypeId ID do tipo de combustível
     * @return lista de bombas
     */
    @Query("SELECT DISTINCT p FROM FuelPump p JOIN FETCH p.fuelTypes ft WHERE ft.id = :fuelTypeId")
    List<FuelPump> findAllByFuelTypeIdWithFuelTypes(@Param("fuelTypeId") Long fuelTypeId);

    /**
     * Busca todas as bombas com seus tipos de combustível carregados em uma única query.
     * Usa DISTINCT para evitar resultados duplicados.
     */
    @Query("SELECT DISTINCT p FROM FuelPump p JOIN FETCH p.fuelTypes")
    List<FuelPump> findAllWithFuelType();

    @Query("SELECT p.id FROM FuelPump p")
    Page<Long> findPageIds(Pageable pageable);

    @Query("SELECT DISTINCT p FROM FuelPump p LEFT JOIN FETCH p.fuelTypes WHERE p.id IN :ids")
    List<FuelPump> findAllByIdInWithFuelTypes(@Param("ids") Collection<Long> ids);
}


