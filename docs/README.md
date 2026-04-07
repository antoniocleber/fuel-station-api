# ⛽ Fuel Station API

API RESTful para gerenciamento de abastecimentos em posto de combustível.
Desenvolvida como solução para o **Desafio Técnico Júnior #1**.

---

## 🏗️ Arquitetura

O projeto segue a **Layered Architecture** (Arquitetura em Camadas):

```
Controller  →  Service  →  Repository  →  Database
    ↑              ↑
  DTOs          Entities
  (request/    (JPA)
   response)
```

### Camadas

| Camada | Pacote | Responsabilidade |
|---|---|---|
| **Controller** | `controller` | Recebe requisições HTTP, valida entrada, delega ao Service |
| **Service** | `service` | Regras de negócio, transações, orquestração |
| **Repository** | `repository` | Acesso a dados via Spring Data JPA |
| **Entity** | `model/entity` | Mapeamento objeto-relacional (JPA) |
| **DTO** | `model/dto` | Objetos de transferência (request/response) |
| **Mapper** | `mapper` | Conversão Entity ↔ DTO via MapStruct |
| **Exception** | `exception` | Tratamento global de erros |
| **Config** | `config` | Configurações do Spring (OpenAPI, etc.) |

---

## 🛠️ Stack Tecnológica

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 | Linguagem |
| Spring Boot | 3.5.0 | Framework principal |
| Gradle (Kotlin DSL) | 8.14 | Build tool |
| Spring Data JPA | 3.5.x | Persistência |
| Hibernate | 6.x | ORM |
| H2 Database | 2.x | Banco embarcado (arquivo) |
| Flyway | 10.x | Migrations de banco |
| MapStruct | 1.6.3 | Mapeamento DTO ↔ Entity |
| Lombok | 1.18.x | Redução de boilerplate |
| SpringDoc OpenAPI | 2.8.8 | Documentação Swagger UI |
| Spring Actuator | 3.5.x | Monitoramento |
| JUnit 5 | 5.x | Testes unitários |
| Mockito | 5.x | Mocks em testes |
| AssertJ | 3.x | Asserções fluentes |
| Checkstyle | 10.21.4 | Análise estática |
| JaCoCo | — | Cobertura de testes |

---

## 📁 Estrutura de Arquivos

```
fuel-station/
├── config/
│   └── checkstyle/
│       └── checkstyle.xml          # Regras de estilo de código
├── gradle/wrapper/
│   └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/com/fuelstation/
│   │   │   ├── FuelStationApplication.java
│   │   │   ├── config/
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── FuelTypeController.java
│   │   │   │   ├── FuelPumpController.java
│   │   │   │   └── FuelingController.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── ConflictException.java
│   │   │   │   └── BusinessException.java
│   │   │   ├── mapper/
│   │   │   │   ├── FuelTypeMapper.java
│   │   │   │   ├── FuelPumpMapper.java
│   │   │   │   └── FuelingMapper.java
│   │   │   ├── model/
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── FuelTypeRequest.java
│   │   │   │   │   │   ├── FuelPumpRequest.java
│   │   │   │   │   │   └── FuelingRequest.java
│   │   │   │   │   └── response/
│   │   │   │   │       ├── FuelTypeResponse.java
│   │   │   │   │       ├── FuelPumpResponse.java
│   │   │   │   │       ├── FuelingResponse.java
│   │   │   │   │       └── ApiErrorResponse.java
│   │   │   │   └── entity/
│   │   │   │       ├── FuelType.java
│   │   │   │       ├── FuelPump.java
│   │   │   │       └── Fueling.java
│   │   │   ├── repository/
│   │   │   │   ├── FuelTypeRepository.java
│   │   │   │   ├── FuelPumpRepository.java
│   │   │   │   └── FuelingRepository.java
│   │   │   └── service/
│   │   │       ├── FuelTypeService.java
│   │   │       ├── FuelPumpService.java
│   │   │       └── FuelingService.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   │           ├── V1__create_initial_schema.sql
│   │           └── V2__seed_initial_data.sql
│   └── test/
│       └── java/com/fuelstation/
│           ├── controller/
│           │   ├── FuelTypeControllerTest.java
│           │   └── FuelingControllerTest.java
│           └── service/
│               ├── FuelTypeServiceTest.java
│               ├── FuelPumpServiceTest.java
│               └── FuelingServiceTest.java
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Como executar

### Pré-requisitos

- **Java 21+** instalado
- **Git**

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-usuario/fuel-station.git
cd fuel-station
```

### 2. Executar a aplicação

```bash
# Linux / macOS
./gradlew bootRun

# Windows
gradlew.bat bootRun
```

A aplicação sobe em **http://localhost:8080**.

---

## 📖 Documentação da API

Após subir a aplicação, acesse:

| URL | Descrição |
|---|---|
| `http://localhost:8080/swagger-ui.html` | Swagger UI (interface interativa) |
| `http://localhost:8080/api-docs` | Especificação OpenAPI 3.0 (JSON) |
| `http://localhost:8080/h2-console` | Console H2 (banco de dados) |
| `http://localhost:8080/actuator/health` | Health check |

### Credenciais H2 Console

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:file:./data/fuelstation` |
| User Name | `sa` |
| Password | *(vazio)* |

---

## 🔌 Endpoints

### Tipos de Combustível — `/api/v1/fuel-types`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/fuel-types` | Lista paginada (`page`, `size`, `sort`) |
| `GET` | `/api/v1/fuel-types/{id}` | Busca por ID |
| `POST` | `/api/v1/fuel-types` | Cria novo |
| `PUT` | `/api/v1/fuel-types/{id}` | Atualiza |
| `DELETE` | `/api/v1/fuel-types/{id}` | Remove |

### Bombas de Combustível — `/api/v1/fuel-pumps`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/fuel-pumps` | Lista paginada com combustíveis (`page`, `size`, `sort`) |
| `GET` | `/api/v1/fuel-pumps/{id}` | Busca por ID |
| `POST` | `/api/v1/fuel-pumps` | Cria nova |
| `PUT` | `/api/v1/fuel-pumps/{id}` | Atualiza |
| `DELETE` | `/api/v1/fuel-pumps/{id}` | Remove |

### Abastecimentos — `/api/v1/fuelings`

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/api/v1/fuelings` | Lista paginada (filtros opcionais + `page`, `size`, `sort`) |
| `GET` | `/api/v1/fuelings?pumpId=1&startDate=2025-01-01&endDate=2025-01-31&page=0&size=20&sort=fuelingDate,desc` | Filtra e pagina |
| `GET` | `/api/v1/fuelings/{id}` | Busca por ID |
| `POST` | `/api/v1/fuelings` | Registra abastecimento |
| `PUT` | `/api/v1/fuelings/{id}` | Atualiza |
| `DELETE` | `/api/v1/fuelings/{id}` | Remove |

### Formato de resposta de listagem (PageResponse)

```json
{
  "content": [],
  "page": 0,
  "size": 20,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true
}
```

---

## 📦 Exemplos de uso (cURL)

### Criar um tipo de combustível

```bash
curl -X POST http://localhost:8080/api/v1/fuel-types \
  -H "Content-Type: application/json" \
  -d '{"name": "Gasolina Premium", "pricePerLiter": 6.499}'
```

### Criar uma bomba

```bash
curl -X POST http://localhost:8080/api/v1/fuel-pumps \
  -H "Content-Type: application/json" \
  -d '{"name": "Bomba 06 - Premium", "fuelTypeIds": [1]}'
```

### Registrar um abastecimento

```bash
curl -X POST http://localhost:8080/api/v1/fuelings \
  -H "Content-Type: application/json" \
  -d '{
    "pumpId": 1,
    "fuelTypeId": 1,
    "fuelingDate": "2025-03-25",
    "liters": 35.500,
    "totalValue": 209.22
  }'
```

### Filtrar abastecimentos por período

```bash
curl "http://localhost:8080/api/v1/fuelings?startDate=2025-01-01&endDate=2025-01-31&page=0&size=20&sort=fuelingDate,desc"
```

---

## 🧪 Testes

```bash
# Executar todos os testes
./gradlew test

# Gerar relatório de cobertura (JaCoCo)
./gradlew jacocoTestReport

# Verificar cobertura mínima (70%)
./gradlew jacocoTestCoverageVerification

# Verificar estilo de código (Checkstyle)
./gradlew checkstyleMain

# Executar tudo junto
./gradlew check
```

Relatórios gerados em:
- **Testes:** `build/reports/tests/test/index.html`
- **Cobertura:** `build/reports/jacoco/test/html/index.html`

---

## 🗃️ Persistência dos Dados

Os dados **persistem entre restarts** da aplicação. O banco H2 é armazenado em arquivo no diretório `./data/fuelstation.mv.db`.

As **migrations Flyway** garantem que o schema seja criado e mantido de forma versionada:

| Migration | Descrição |
|---|---|
| `V1__create_initial_schema.sql` | Criação das tabelas com constraints |
| `V2__seed_initial_data.sql` | Dados iniciais de demonstração |

---

## 🔒 Regras de Negócio

| Regra | Comportamento |
|---|---|
| Nome de combustível único | `409 Conflict` se duplicado |
| Nome de bomba único | `409 Conflict` se duplicado |
| Combustível com bombas associadas | `422` bloqueia exclusão |
| Bomba com abastecimentos registrados | `422` bloqueia exclusão |
| Data de abastecimento futura | `400 Bad Request` |
| Litragem / valor ≤ 0 | `400 Bad Request` |

---

## 💡 Decisões Técnicas

**Gradle Kotlin DSL** em vez de Groovy: type-safety, autocomplete em IDEs e melhor legibilidade.

**Flyway** em vez de `ddl-auto=create`: garante controle de versão do schema e dados seguros em produção.

**MapStruct** em vez de conversão manual: mapeamento gerado em tempo de compilação, zero reflection, sem risco de NullPointerException silencioso.

**H2 em modo arquivo**: sem necessidade de servidor externo, com persistência real entre reinicializações.

**`open-in-view: false`**: evita o anti-pattern de queries Hibernate disparadas durante a serialização JSON (N+1 oculto).

**JOIN FETCH nas queries de listagem**: todas as consultas que retornam dados aninhados usam `JOIN FETCH` para evitar o problema N+1.
