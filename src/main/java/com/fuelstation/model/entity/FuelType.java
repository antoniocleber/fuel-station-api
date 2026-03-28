package com.fuelstation.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa um tipo de combustível no sistema.
 *
 * <p>Exemplo: Gasolina Comum, Etanol, Diesel S10.</p>
 * <p>Uma bomba pode ter múltiplos tipos de combustível associados.</p>
 */
@Entity
@Table(name = "fuel_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "pumps")
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /** Nome do tipo de combustível (deve ser único). */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** Preço por litro com precisão de 3 casas decimais. */
    @Column(name = "price_per_liter", nullable = false, precision = 10, scale = 3)
    private BigDecimal pricePerLiter;

    /** Conjunto de bombas que utilizam este tipo de combustível (relacionamento ManyToMany). */
    @ManyToMany(mappedBy = "fuelTypes", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<FuelPump> pumps = new HashSet<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
