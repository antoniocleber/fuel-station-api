# Fuel Station API

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-green)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.x-blue)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

API REST para gerenciamento de um posto de combustivel. O projeto cobre cadastro de tipos de combustivel, bombas, abastecimentos, relatorios agregados, migracoes de banco com Flyway e documentacao OpenAPI.

## Visao Geral

### Principais capacidades

- CRUD de tipos de combustivel
- CRUD de bombas com multiplos tipos de combustivel
- CRUD de abastecimentos com filtros por bomba e periodo
- Relatorio consolidado de abastecimentos por bomba
- Paginacao padrao nos endpoints de listagem
- Validacoes de negocio e tratamento centralizado de erros
- Swagger UI, H2 Console e Actuator para suporte operacional

### Stack

- Java 21
- Spring Boot 3.5.0
- Spring Web
- Spring Data JPA
- Spring Validation
- Flyway
- H2 Database
- MapStruct
- Lombok
- Springdoc OpenAPI
- JUnit 5, Mockito, JaCoCo e Checkstyle

## Estrutura do Projeto

```text
fuel-station/
├── src/main/java/com/fuelstation
│   ├── config
│   ├── controller
│   ├── exception
│   ├── mapper
│   ├── model
│   ├── repository
│   └── service
├── src/main/resources
│   ├── application.yml
│   └── db/migration
├── src/test/java/com/fuelstation
└── docs
```

## Como Executar

### Pre-requisitos

- JDK 21
- PowerShell ou terminal com permissao para executar o Gradle Wrapper

### Subir localmente

```bash
git clone <repo-url>
cd fuel-station
./gradlew clean test
./gradlew bootRun
```

No Windows, se preferir:

```powershell
gradlew.bat clean test
gradlew.bat bootRun
```

### Endpoints locais

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/api-docs`
- H2 Console: `http://localhost:8080/h2-console`
- Actuator Health: `http://localhost:8080/actuator/health`
- Actuator Info: `http://localhost:8080/actuator/info`

## Configuracao

As configuracoes principais estao em `src/main/resources/application.yml`.

### Banco local

- URL: `jdbc:h2:file:./data/fuelstation;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`
- Usuario: `sa`
- Senha: vazia

### CORS

O backend permite, por padrao, chamadas vindas de `http://localhost:5173`, conforme [CorsConfig.java](src/main/java/com/fuelstation/config/CorsConfig.java).

### Documentacao e observabilidade

- Swagger habilitado por Springdoc
- Actuator com `health`, `info` e `metrics`
- Flyway executado automaticamente na inicializacao

## Endpoints Principais

### Tipos de combustivel

- `GET /api/v1/fuel-types`
- `GET /api/v1/fuel-types/{id}`
- `POST /api/v1/fuel-types`
- `PUT /api/v1/fuel-types/{id}`
- `DELETE /api/v1/fuel-types/{id}`

### Bombas

- `GET /api/v1/fuel-pumps`
- `GET /api/v1/fuel-pumps/{id}`
- `POST /api/v1/fuel-pumps`
- `PUT /api/v1/fuel-pumps/{id}`
- `DELETE /api/v1/fuel-pumps/{id}`

### Abastecimentos

- `GET /api/v1/fuelings`
- `GET /api/v1/fuelings/{id}`
- `POST /api/v1/fuelings`
- `PUT /api/v1/fuelings/{id}`
- `DELETE /api/v1/fuelings/{id}`

Filtros suportados em `GET /api/v1/fuelings`:

- `pumpId`
- `startDate`
- `endDate`
- `page`
- `size`
- `sort`

### Relatorios

- `GET /api/v1/reports/fuelings`

Filtros suportados:

- `pumpId`
- `startDate`
- `endDate`

## Paginacao

Os endpoints de listagem retornam `PageResponse<T>`.

### Parametros comuns

- `page` com default `0`
- `size` com default `20`
- `sort`, por exemplo `sort=name,asc` ou `sort=fuelingDate,desc`

### Exemplo de resposta

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

## Testes e Qualidade

### Comandos uteis

```bash
./gradlew test
./gradlew jacocoTestReport
./gradlew jacocoTestCoverageVerification
./gradlew checkstyleMain
./gradlew checkstyleTest
```

### O que ja esta configurado

- JUnit 5 como plataforma de testes
- JaCoCo com geracao de relatorio HTML e XML
- Verificacao minima de cobertura em `70%`
- Checkstyle com configuracao dedicada em `config/checkstyle/checkstyle.xml`

## Documentacao Complementar

A pasta `docs/` concentra material adicional. Pontos de entrada mais uteis:

- [docs/00_COMECE_AQUI.md](docs/00_COMECE_AQUI.md)
- [docs/START_HERE.md](docs/START_HERE.md)
- [docs/API_USAGE_EXAMPLES.md](docs/API_USAGE_EXAMPLES.md)
- [docs/VALIDATION_GUIDE.md](docs/VALIDATION_GUIDE.md)
- [docs/DOCUMENTATION_INDEX.md](docs/DOCUMENTATION_INDEX.md)
- [docs/AGENTS.md](docs/AGENTS.md)

## Melhorias Futuras Recomendadas

- Externalizar origens CORS para variavel de ambiente ou perfil
- Documentar exemplos de payload de erro e regras de validacao no README
- Adicionar secao de deploy com perfil para PostgreSQL
- Incluir comando de bootstrap integrado com frontend em ambiente local
- Versionar exemplos de `.env` e contratos de integracao frontend/backend

## Licenca

MIT. Veja [LICENSE](LICENSE).
