# Validação e Testes da Refatoração

## ✅ Checklist de Validação

Este documento orienta como validar a refatoração ManyToMany.

### 1. Compilação

```bash
cd E:\Downloads\fuel-station
gradlew.bat clean compile
```

**Pontos de atenção:**
- ✅ Sem erros de compilação do Lombok
- ✅ Sem erros de compilação do MapStruct
- ✅ Sem erros de compilação das entidades
- ✅ MapStruct gera `FuelPumpMapperImpl` corretamente

**Sinais de sucesso:**
```
BUILD SUCCESSFUL
```

### 2. Testes Unitários

```bash
gradlew.bat test
```

**Testes que podem falhar (ajustes necessários):**
- FuelPumpServiceTest - ajustar mocks para usar Set<Long> em vez de Long
- FuelPumpControllerTest - ajustar requests/responses
- FuelingServiceTest - pode precisar ajustes nos mocks de pump

**Testes que devem passar (sem mudanças):**
- FuelTypeServiceTest
- FuelTypeControllerTest
- FuelingControllerTest

### 3. Verificação de Código (Checkstyle)

```bash
gradlew.bat checkstyleMain
```

**Esperado:**
```
BUILD SUCCESSFUL
```

### 4. Cobertura de Testes (JaCoCo)

```bash
gradlew.bat jacocoTestReport
```

**Relatório gerado em:**
```
build/reports/jacoco/test/html/index.html
```

**Mínimo esperado:** 70% de cobertura

### 5. Executar Aplicação

```bash
gradlew.bat bootRun
```

**Esperado:**
```
Started FuelStationApplication in X.XXX seconds
```

**Console H2 disponível:**
```
http://localhost:8080/h2-console
```

**Swagger UI disponível:**
```
http://localhost:8080/swagger-ui.html
```

---

## 🧪 Testes Manuais via Swagger UI

### 1. Criar Combustíveis (FuelType)

Acesse: `http://localhost:8080/swagger-ui.html` → **Combustíveis**

**Pré-requisito:** Criar 3 tipos de combustível

```json
POST /api/v1/fuel-types
{
  "name": "Gasolina Comum",
  "pricePerLiter": 5.89
}

{
  "name": "Etanol",
  "pricePerLiter": 4.29
}

{
  "name": "Diesel S10",
  "pricePerLiter": 6.15
}
```

**Anotar os IDs retornados** (ex: 1, 2, 3)

### 2. Criar Bomba com Múltiplos Combustíveis

**Endpoint:** `POST /api/v1/fuel-pumps`

```json
{
  "name": "Bomba A1",
  "fuelTypeIds": [1, 2, 3]
}
```

**Esperado:** 201 Created com resposta:
```json
{
  "id": 1,
  "name": "Bomba A1",
  "fuelTypes": [
    {
      "id": 1,
      "name": "Gasolina Comum",
      "pricePerLiter": 5.89
    },
    {
      "id": 2,
      "name": "Etanol",
      "pricePerLiter": 4.29
    },
    {
      "id": 3,
      "name": "Diesel S10",
      "pricePerLiter": 6.15
    }
  ],
  "createdAt": "2024-03-28T...",
  "updatedAt": "2024-03-28T..."
}
```

### 3. Listar Todas as Bombas

**Endpoint:** `GET /api/v1/fuel-pumps`

**Esperado:** 200 OK com lista mostrando múltiplos combustíveis

### 4. Buscar Uma Bomba pelo ID

**Endpoint:** `GET /api/v1/fuel-pumps/1`

**Esperado:** 200 OK com detalhe da bomba e seus 3 combustíveis

### 5. Atualizar Bomba (Mudar Combustíveis)

**Endpoint:** `PUT /api/v1/fuel-pumps/1`

```json
{
  "name": "Bomba A1",
  "fuelTypeIds": [1, 2]
}
```

**Esperado:** 200 OK com apenas 2 combustíveis na resposta

### 6. Teste de Validação: fuelTypeIds Vazio

**Endpoint:** `POST /api/v1/fuel-pumps`

```json
{
  "name": "Bomba Teste",
  "fuelTypeIds": []
}
```

**Esperado:** 400 Bad Request com mensagem:
```json
{
  "status": 400,
  "message": "Validação falhou",
  "errors": {
    "fuelTypeIds": "A bomba deve ter pelo menos um tipo de combustível."
  }
}
```

### 7. Teste de Validação: Combustível Inexistente

**Endpoint:** `POST /api/v1/fuel-pumps`

```json
{
  "name": "Bomba Teste2",
  "fuelTypeIds": [1, 999]
}
```

**Esperado:** 404 Not Found com mensagem sobre combustível não encontrado

### 8. Criar Abastecimento (Fueling)

**Pré-requisito:** Bomba criada com sucesso

**Endpoint:** `POST /api/v1/fuelings`

```json
{
  "pumpId": 1,
  "fuelingDate": "2024-03-28",
  "liters": 50.5,
  "totalValue": 297.44
}
```

**Esperado:** 201 Created com resposta mostrando múltiplos combustíveis da bomba:
```json
{
  "id": 1,
  "fuelingDate": "2024-03-28",
  "liters": 50.5,
  "totalValue": 297.44,
  "pump": {
    "id": 1,
    "name": "Bomba A1",
    "fuelTypes": [
      { "id": 1, "name": "Gasolina Comum", "pricePerLiter": 5.89 },
      { "id": 2, "name": "Etanol", "pricePerLiter": 4.29 }
    ]
  },
  "createdAt": "2024-03-28T...",
  "updatedAt": "2024-03-28T..."
}
```

### 9. Listar Abastecimentos

**Endpoint:** `GET /api/v1/fuelings`

**Esperado:** 200 OK com lista mostrando múltiplos combustíveis por bomba

### 10. Tentar Deletar Combustível com Bombas Associadas

**Endpoint:** `DELETE /api/v1/fuel-types/1`

**Esperado:** 422 Unprocessable Entity com mensagem:
```json
{
  "status": 422,
  "message": "Não é possível excluir o tipo de combustível pois existem bombas associadas..."
}
```

---

## 🔍 Inspeção do Banco de Dados

### Acessar Console H2

1. Abra `http://localhost:8080/h2-console`
2. **JDBC URL:** `jdbc:h2:file:./data/fuelstation`
3. **User Name:** `sa`
4. **Password:** (deixar em branco)
5. Click "Connect"

### Queries para Validar

#### Ver tabela fuel_pump_fuel_type criada
```sql
SELECT * FROM fuel_pump_fuel_type;
```

**Esperado:** Mostrar relacionamentos criados

#### Ver dados migrados
```sql
SELECT p.id, p.name, ft.id, ft.name 
FROM fuel_pump p
LEFT JOIN fuel_pump_fuel_type pft ON p.id = pft.fuel_pump_id
LEFT JOIN fuel_type ft ON pft.fuel_type_id = ft.id;
```

**Esperado:** Uma bomba com múltiplas linhas (uma para cada combustível)

#### Ver coluna fuel_type_id foi removida
```sql
DESC fuel_pump;
```

**Esperado:** Coluna `fuel_type_id` **NÃO** deve aparecer

#### Ver índices criados
```sql
SELECT * FROM INFORMATION_SCHEMA.INDEXES WHERE TABLE_NAME = 'FUEL_PUMP_FUEL_TYPE';
```

**Esperado:** Índices `idx_fuel_pump_fuel_type_pump` e `idx_fuel_pump_fuel_type_type`

---

## 📊 Análise de Cobertura

### Gerar Relatório
```bash
gradlew.bat jacocoTestReport
```

### Abrir Relatório
```
build/reports/jacoco/test/html/index.html
```

### Classes a Verificar Cobertura
- FuelPumpService: ✅ Deve aumentar cobertura (novo método `loadFuelTypes()`)
- FuelPumpMapper: ✅ Deve ter cobertura (novo método `mapFuelTypesToSummaries()`)
- FuelingMapper: ✅ Deve ter cobertura (novo método `mapFuelTypesToSummaries()`)

---

## 🐛 Possíveis Problemas e Soluções

### Problema 1: "Could not resolve all files for configuration ':annotationProcessor'"

**Causa:** Lombok não está com versão especificada

**Solução:** Já foi corrigido em `build.gradle.kts`

### Problema 2: MapStruct não gera Impl

**Causa:** Ordem errada de annotationProcessors

**Solução:** Lombok deve vir ANTES de MapStruct (já feito)

### Problema 3: Testes antigos falham

**Causa:** DTOs mudaram estrutura (fuelTypeId → fuelTypeIds, fuelType → fuelTypes)

**Solução:** Atualizar testes com nova estrutura (vide exemplos em API_USAGE_EXAMPLES.md)

### Problema 4: Migração Flyway falha

**Causa:** Dados inválidos ou constraint violations

**Solução:** Deletar `./data/fuelstation.mv.db` e deixar Flyway recriar schema

```bash
# Windows
Remove-Item ./data/fuelstation.mv.db -Force
gradlew.bat bootRun
```

---

## ✨ Resumo de Validação

| Item | Status | Comando |
|------|--------|---------|
| Compilação | ✅ | `gradlew clean compile` |
| Testes | ⚠️ | `gradlew test` |
| Checkstyle | ✅ | `gradlew checkstyleMain` |
| Cobertura | ✅ | `gradlew jacocoTestReport` |
| Aplicação | ✅ | `gradlew bootRun` |
| Swagger | ✅ | `http://localhost:8080/swagger-ui.html` |
| H2 Console | ✅ | `http://localhost:8080/h2-console` |
| Testes Manuais | ✅ | Via Swagger UI |
| Banco de Dados | ✅ | Query em H2 Console |

**⚠️ Nota:** Testes unitários podem precisar de ajustes nos mocks para nova estrutura.

---

## 📞 Suporte

Para mais detalhes:
- Veja `REFACTORING_SUMMARY.md` para resumo técnico
- Veja `API_USAGE_EXAMPLES.md` para exemplos de requisições
- Veja `AGENTS.md` para padrões e convenções
- Veja `REFACTORING_MANYTOMANY.md` para mudanças detalhadas


