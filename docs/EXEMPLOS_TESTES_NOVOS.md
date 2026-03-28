# 📝 Exemplo Prático dos Novos Testes

## Novo Teste 1: Criar Bomba com Múltiplos Combustíveis

```java
@Test
@DisplayName("deve criar bomba com múltiplos combustíveis")
void shouldCreateWithMultipleFuels() {
    // Setup
    given(fuelPumpRepository.existsByName("Bomba 01")).willReturn(false);
    given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);
    given(fuelTypeService.findEntityById(2L)).willReturn(etanol);
    given(fuelPumpMapper.toEntity(bomba1Request)).willReturn(bomba1);
    given(fuelPumpRepository.save(bomba1)).willReturn(bomba1);
    given(fuelPumpMapper.toResponse(bomba1)).willReturn(bomba1Response);

    // Ação
    FuelPumpResponse result = fuelPumpService.create(bomba1Request);

    // Assertivas
    assertThat(result.getName()).isEqualTo("Bomba 01");
    then(fuelTypeService).should(times(2)).findEntityById(any());
    then(fuelPumpRepository).should().save(any(FuelPump.class));
}
```

### O que valida:
✅ Bomba pode ser criada com 2 ou mais combustíveis  
✅ Todos os combustíveis são validados (times(2))  
✅ Repository é chamado para salvar  

### Entrada:
```json
{
  "name": "Bomba 01",
  "fuelTypeIds": [1, 2]
}
```

### Saída esperada:
```json
{
  "id": 1,
  "name": "Bomba 01",
  "fuelTypes": [
    { "id": 1, "name": "Gasolina", ... },
    { "id": 2, "name": "Etanol", ... }
  ]
}
```

---

## Novo Teste 2: Erro - Criar Bomba SEM Combustíveis

```java
@Test
@DisplayName("deve lançar BusinessException quando fuelTypeIds está vazio")
void shouldThrowWhenNoFuelTypes() {
    // Setup
    FuelPumpRequest request = new FuelPumpRequest();
    request.setName("Bomba Inválida");
    request.setFuelTypeIds(Set.of());  // ← VAZIO!

    // Ação & Assertiva
    assertThatThrownBy(() -> fuelPumpService.create(request))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("pelo menos um tipo de combustível");

    // Verifica que não tentou salvar
    then(fuelPumpRepository).should(never()).save(any());
}
```

### O que valida:
✅ Bomba com ZERO combustíveis é rejeitada  
✅ Exceção BusinessException é lançada  
✅ Mensagem clara para o usuário  
✅ Nenhuma tentativa de salvar  

### Entrada:
```json
{
  "name": "Bomba Inválida",
  "fuelTypeIds": []  ← VAZIO!
}
```

### Saída esperada:
```json
{
  "status": 400,
  "message": "Uma bomba deve ter pelo menos um tipo de combustível."
}
```

---

## Novo Teste 3: Atualizar Combustíveis de uma Bomba

```java
@Test
@DisplayName("deve atualizar combustíveis de uma bomba")
void shouldUpdateFuelTypes() {
    // Setup
    FuelPumpRequest updateRequest = new FuelPumpRequest();
    updateRequest.setName("Bomba 01");
    updateRequest.setFuelTypeIds(Set.of(1L));  // Muda para 1 combustível

    given(fuelPumpRepository.findById(1L)).willReturn(Optional.of(bomba1));
    given(fuelPumpRepository.existsByNameAndIdNot("Bomba 01", 1L)).willReturn(false);
    given(fuelTypeService.findEntityById(1L)).willReturn(gasolina);
    given(fuelPumpMapper.toEntity(updateRequest)).willReturn(bomba1);
    given(fuelPumpRepository.save(bomba1)).willReturn(bomba1);
    given(fuelPumpMapper.toResponse(bomba1)).willReturn(bomba1Response);

    // Ação
    FuelPumpResponse result = fuelPumpService.update(1L, updateRequest);

    // Assertivas
    assertThat(result.getName()).isEqualTo("Bomba 01");
    then(fuelPumpRepository).should().save(any(FuelPump.class));
}
```

### O que valida:
✅ Combustíveis de uma bomba podem ser atualizados  
✅ Bomba existente é encontrada  
✅ Repository é chamado para salvar  

### Entrada:
```json
PUT /api/v1/fuel-pumps/1
{
  "name": "Bomba 01",
  "fuelTypeIds": [1]  ← Mudou de [1,2] para [1]
}
```

### Saída esperada:
```json
{
  "id": 1,
  "name": "Bomba 01",
  "fuelTypes": [
    { "id": 1, "name": "Gasolina", ... }
  ]
}
```

---

## Novo Teste 4: Erro - Atualizar Bomba para ZERO Combustíveis

```java
@Test
@DisplayName("deve lançar BusinessException quando fuelTypeIds fica vazio na atualização")
void shouldThrowWhenNoFuelTypesInUpdate() {
    // Setup
    FuelPumpRequest updateRequest = new FuelPumpRequest();
    updateRequest.setName("Bomba 01");
    updateRequest.setFuelTypeIds(Set.of());  // ← VAZIO!

    // Ação & Assertiva
    assertThatThrownBy(() -> fuelPumpService.update(1L, updateRequest))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("pelo menos um tipo de combustível");

    // Verifica que não tentou salvar
    then(fuelPumpRepository).should(never()).save(any());
}
```

### O que valida:
✅ Não é possível remover TODOS os combustíveis  
✅ Exceção BusinessException é lançada  
✅ Mensagem clara para o usuário  
✅ Nenhuma tentativa de salvar  

### Entrada:
```json
PUT /api/v1/fuel-pumps/1
{
  "name": "Bomba 01",
  "fuelTypeIds": []  ← VAZIO!
}
```

### Saída esperada:
```json
{
  "status": 400,
  "message": "Uma bomba deve ter pelo menos um tipo de combustível."
}
```

---

## Setup dos Testes (Shared)

```java
@BeforeEach
void setUp() {
    // Criar 2 combustíveis diferentes
    gasolina = FuelType.builder()
            .id(1L).name("Gasolina Comum")
            .pricePerLiter(new BigDecimal("5.890"))
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();

    etanol = FuelType.builder()
            .id(2L).name("Etanol")
            .pricePerLiter(new BigDecimal("4.290"))
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();

    // Bomba com múltiplos combustíveis
    Set<FuelType> fuelTypes = new HashSet<>();
    fuelTypes.add(gasolina);
    fuelTypes.add(etanol);

    bomba1 = FuelPump.builder()
            .id(1L).name("Bomba 01")
            .fuelTypes(fuelTypes)
            .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
            .build();

    // Request com múltiplos combustíveis
    bomba1Request = new FuelPumpRequest();
    bomba1Request.setName("Bomba 01");
    bomba1Request.setFuelTypeIds(Set.of(1L, 2L));
}
```

---

## Estrutura de Testes Aninhada

```
FuelPumpServiceTest
├── Create
│   ├── shouldCreateWithMultipleFuels() ........... ✅ Novo
│   ├── shouldThrowWhenNoFuelTypes() ............ ✅ Novo (Regra)
│   ├── shouldThrowWhenNameExists()
│   └── shouldThrowWhenFuelTypeNotFound()
│
├── Update ..................................... ✅ Novo Nested Class
│   ├── shouldUpdateFuelTypes() ................. ✅ Novo
│   └── shouldThrowWhenNoFuelTypesInUpdate() ... ✅ Novo (Regra)
│
├── FindAll
│   └── shouldReturnAllPumps()
│
└── Delete
    ├── shouldDeleteWithNoFuelings()
    └── shouldThrowWhenHasFuelings()
```

---

## Sumário de Casos Testados

| Caso | Teste | Esperado |
|------|-------|----------|
| ✅ Criar com 2 combustíveis | shouldCreateWithMultipleFuels | 201 Created |
| ❌ Criar sem combustíveis | shouldThrowWhenNoFuelTypes | 400 Bad Request |
| ❌ Nome duplicado | shouldThrowWhenNameExists | 409 Conflict |
| ❌ Combustível não existe | shouldThrowWhenFuelTypeNotFound | 404 Not Found |
| ✅ Atualizar combustíveis | shouldUpdateFuelTypes | 200 OK |
| ❌ Atualizar para zero | shouldThrowWhenNoFuelTypesInUpdate | 400 Bad Request |
| ✅ Listar todas | shouldReturnAllPumps | 200 OK |
| ✅ Deletar sem abastecimentos | shouldDeleteWithNoFuelings | 204 No Content |
| ❌ Deletar com abastecimentos | shouldThrowWhenHasFuelings | 422 Unprocessable |

---

## Execução

```bash
# Executar todos os testes
gradlew.bat test

# Executar apenas FuelPumpServiceTest
gradlew.bat test --tests FuelPumpServiceTest

# Executar teste específico
gradlew.bat test --tests FuelPumpServiceTest.Create.shouldThrowWhenNoFuelTypes

# Com saída verbosa
gradlew.bat test -i
```

---

**Total de novos testes:** 4  
**Total de testes modificados:** 2  
**Total de testes consolidados:** 9 (sem redundâncias)  

✅ Todos os testes estão prontos para execução!


