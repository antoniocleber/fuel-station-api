package com.fuelstation.repository;

import com.fuelstation.model.entity.Fueling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para a entidade {@link Fueling}.
 */
@Repository
public interface FuelingRepository extends JpaRepository<Fueling, Long> {

    /**
     * Verifica se há abastecimentos registrados para determinada bomba.
     * Usado para impedir exclusão de bomba em uso.
     *
     * @param pumpId ID da bomba
     * @return {@code true} se existir ao menos um abastecimento
     */
    boolean existsByPumpId(Long pumpId);

    /**
     * Lista todos os abastecimentos com join fetch para evitar N+1 queries
     * ao serializar pump e fuelTypes (ManyToMany).
     * Usa DISTINCT para evitar duplicatas quando faz JOIN em coleção.
     */
    @Query("""
            SELECT DISTINCT f FROM Fueling f
            JOIN FETCH f.pump p
            LEFT JOIN FETCH p.fuelTypes
            ORDER BY f.fuelingDate DESC
            """)
    List<Fueling> findAllWithDetails();

    /**
     * Busca abastecimento por ID com todos os relacionamentos carregados.
     *
     * @param id ID do abastecimento
     * @return Optional com o abastecimento
     */
    @Query("""
            SELECT DISTINCT f FROM Fueling f
            JOIN FETCH f.pump p
            LEFT JOIN FETCH p.fuelTypes
            WHERE f.id = :id
            """)
    Optional<Fueling> findByIdWithDetails(@Param("id") Long id);

    /**
     * Filtra abastecimentos por bomba e/ou intervalo de datas.
     * Usa DISTINCT para evitar duplicatas quando faz JOIN em coleção (fuelTypes).
     *
     * @param pumpId    ID da bomba (nullable)
     * @param startDate data inicial (nullable)
     * @param endDate   data final (nullable)
     * @return lista filtrada
     */
    @Query("""
            SELECT DISTINCT f FROM Fueling f
            JOIN FETCH f.pump p
            LEFT JOIN FETCH p.fuelTypes
            WHERE (:pumpId IS NULL OR p.id = :pumpId)
              AND (:startDate IS NULL OR f.fuelingDate >= :startDate)
              AND (:endDate IS NULL OR f.fuelingDate <= :endDate)
            ORDER BY f.fuelingDate DESC
            """)
    List<Fueling> findWithFilters(
            @Param("pumpId") Long pumpId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
