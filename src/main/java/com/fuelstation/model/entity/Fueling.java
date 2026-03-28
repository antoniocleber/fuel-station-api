package com.fuelstation.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa um registro de abastecimento.
 *
 * <p>Contém a bomba utilizada, data, volume em litros e valor total cobrado.</p>
 */
@Entity
@Table(name = "fueling")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "pump")
public class Fueling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Bomba em que o abastecimento foi realizado.
     * LAZY para evitar JOIN desnecessário na maioria das consultas.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pump_id", nullable = false)
    private FuelPump pump;

    /** Data em que o abastecimento ocorreu. */
    @Column(name = "fueling_date", nullable = false)
    private LocalDate fuelingDate;

    /** Volume abastecido em litros. */
    @Column(nullable = false, precision = 10, scale = 3)
    private BigDecimal liters;

    /** Valor total cobrado pelo abastecimento. */
    @Column(name = "total_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalValue;

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
