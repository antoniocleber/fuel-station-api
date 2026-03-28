# Refatoração: ManyToMany FuelPump-FuelType

## Resumo das Mudanças

Esta refatoração altera o relacionamento entre `FuelPump` e `FuelType` de **ManyToOne** para **ManyToMany**, permitindo que uma bomba de combustível tenha múltiplos tipos de combustível associados.

### Mudanças de Entidades

#### FuelType.java
```diff
- @OneToMany(mappedBy = "fuelType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
- @Builder.Default
- private List<FuelPump> pumps = new ArrayList<>();

+ @ManyToMany(mappedBy = "fuelTypes", fetch = FetchType.LAZY)
+ @Builder.Default
+ private Set<FuelPump> pumps = new HashSet<>();
```

#### FuelPump.java
```diff
- @ManyToOne(fetch = FetchType.LAZY, optional = false)
- @JoinColumn(name = "fuel_type_id", nullable = false)
- private FuelType fuelType;

+ @ManyToMany(fetch = FetchType.LAZY)
+ @JoinTable(
+     name = "fuel_pump_fuel_type",
+     joinColumns = @JoinColumn(name = "fuel_pump_id"),
+     inverseJoinColumns = @JoinColumn(name = "fuel_type_id")
+ )
+ @Builder.Default
+ private Set<FuelType> fuelTypes = new HashSet<>();
```

### Mudanças de DTOs

#### FuelPumpRequest.java
```diff
- @NotNull(message = "O ID do tipo de combustível é obrigatório.")
- @Positive(message = "O ID do combustível deve ser um número positivo.")
- private Long fuelTypeId;

+ @NotEmpty(message = "A bomba deve ter pelo menos um tipo de combustível.")
+ private Set<@NotNull @Positive Long> fuelTypeIds;
```

#### FuelPumpResponse.java
```diff
- private FuelTypeSummary fuelType;

+ private List<FuelTypeSummary> fuelTypes;
```

### Mudanças de Banco de Dados

#### Migração Flyway V3
- Nova tabela de junção: `fuel_pump_fuel_type`
- Remove coluna `fuel_type_id` de `fuel_pump`
- Mantém dados existentes migrando para a nova estrutura

### Mudanças de Repositório

#### FuelPumpRepository.java
```diff
- @Query("SELECT p FROM FuelPump p JOIN FETCH p.fuelType WHERE p.fuelType.id = :fuelTypeId")
- List<FuelPump> findAllByFuelTypeIdWithFuelType(@Param("fuelTypeId") Long fuelTypeId);

+ @Query("SELECT DISTINCT p FROM FuelPump p JOIN FETCH p.fuelTypes ft WHERE ft.id = :fuelTypeId")
+ List<FuelPump> findAllByFuelTypeIdWithFuelTypes(@Param("fuelTypeId") Long fuelTypeId);

- @Query("SELECT p FROM FuelPump p JOIN FETCH p.fuelType")
- List<FuelPump> findAllWithFuelType();

+ @Query("SELECT DISTINCT p FROM FuelPump p JOIN FETCH p.fuelTypes")
+ List<FuelPump> findAllWithFuelType();
```

### Mudanças de Service

#### FuelPumpService.java
- Método `create()`: agora carrega múltiplos combustíveis via `loadFuelTypes()`
- Método `update()`: idem
- Nova validação: garante que `fuelTypeIds` não está vazio
- Nova validação: cada ID deve corresponder a um FuelType existente

### Mudanças de Mapper

#### FuelPumpMapper.java
- Novo método: `mapFuelTypesToSummaries()` para converter Set<FuelType> → List<FuelTypeSummary>
- Remapeamento automático de coleções para o DTO

#### FuelingMapper.java
- Ajuste: referência a `pump.fuelTypes` em vez de `pump.fuelType`
- Novo método: `mapFuelTypesToSummaries()` para FuelingResponse.PumpSummary

### Mudanças de DTOs de Resposta

#### FuelingResponse.java
```diff
- private FuelTypeSummary fuelType;

+ private List<FuelTypeSummary> fuelTypes;
```

## Regra de Negócio Implementada

**Uma bomba deve ter pelo menos um tipo de combustível associado.**

Esta validação é aplicada em:
- `FuelPumpRequest`: anotação `@NotEmpty`
- `FuelPumpService.create()`: validação programática com exceção `BusinessException`
- `FuelPumpService.update()`: validação programática com exceção `BusinessException`

## Impacto em Outras Entidades

- **Fueling**: Sem mudanças diretas. Continua registrando abastecimentos por bomba.
- **FuelingService**: Sem mudanças na lógica. Apenas os DTOs foram atualizados.

## Migrando Dados Existentes

A migração Flyway V3 automaticamente:
1. Cria tabela `fuel_pump_fuel_type`
2. Copia dados de `fuel_pump.fuel_type_id` para a tabela de junção
3. Remove a coluna `fuel_type_id` e constraint da tabela `fuel_pump`

Após a migração, cada bomba existente terá seu combustível original mantido na relação ManyToMany.

## Arquivo AGENTS.md Atualizado

O arquivo `AGENTS.md` foi atualizado com:
- Seção sobre padrão ManyToMany
- Exemplo de entidade com relacionamento ManyToMany
- Regra de negócio documentada
- Query pattern com `DISTINCT` para evitar duplicatas


