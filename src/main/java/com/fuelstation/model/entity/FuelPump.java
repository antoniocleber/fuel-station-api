package com.fuelstation.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidade que representa uma bomba de combustível no posto.
 *
 * <p>Uma bomba pode estar associada a múltiplos {@link FuelType} (ManyToMany).</p>
 * <p>Regra de negócio: Uma bomba deve ter pelo menos um tipo de combustível associado.</p>
 */
@Entity
@Table(name = "fuel_pump")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"fuelTypes", "fuelings"})
public class FuelPump {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /** Nome identificador da bomba (deve ser único). */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Conjunto de tipos de combustível que esta bomba pode abastecer.
     * Relacionamento ManyToMany - uma bomba pode ter múltiplos combustíveis.
     * LAZY para evitar carregamento desnecessário.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fuel_pump_fuel_type",
        joinColumns = @JoinColumn(name = "fuel_pump_id"),
        inverseJoinColumns = @JoinColumn(name = "fuel_type_id")
    )
    @Builder.Default
    private Set<FuelType> fuelTypes = new HashSet<>();

    /** Histórico de abastecimentos realizados nesta bomba. */
    @OneToMany(mappedBy = "pump", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Fueling> fuelings = new ArrayList<>();

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
