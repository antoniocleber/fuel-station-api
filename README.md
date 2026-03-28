# Fuel Station API

[![Java](https://img.shields.io/badge/Java-18-orange)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)](https://spring.io/projects/spring-boot)
[![Gradle](https://img.shields.io/badge/Gradle-8.5-blue)](https://gradle.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## 📋 Sobre o Projeto

API REST completa para gerenciamento de um posto de combustível, desenvolvida com Spring Boot. Este projeto demonstra uma **refatoração completa** de relacionamento JPA de **ManyToOne para ManyToMany**, incluindo validações de negócio, testes unitários e documentação extensiva.

### 🎯 Funcionalidades

- ✅ **CRUD completo** de tipos de combustível
- ✅ **CRUD completo** de bombas de combustível (com múltiplos tipos)
- ✅ **Registro de abastecimentos** com histórico
- ✅ **Relacionamento ManyToMany** entre bombas e tipos de combustível
- ✅ **Validação de negócio:** bomba deve ter pelo menos 1 tipo de combustível
- ✅ **Documentação Swagger/OpenAPI**
- ✅ **Testes unitários** completos
- ✅ **Migrações Flyway** automáticas

### 🏗️ Arquitetura

- **Backend:** Spring Boot 3.2 + Java 18
- **Banco:** H2 (desenvolvimento) / PostgreSQL (produção)
- **ORM:** JPA/Hibernate com relacionamentos ManyToMany
- **Mapeamento:** MapStruct para conversão DTO
- **Testes:** JUnit 5 + Mockito
- **Documentação:** Swagger/OpenAPI
- **Build:** Gradle

---

## 🚀 Início Rápido

### Pré-requisitos
- Java 18+
- Gradle 8.5+

### Instalação e Execução

```bash
# 1. Clonar repositório
git clone https://github.com/SEU_USERNAME/fuel-station-api.git
cd fuel-station-api

# 2. Compilar
gradlew.bat clean compile

# 3. Executar testes
gradlew.bat test

# 4. Rodar aplicação
gradlew.bat bootRun
```

### Acesso
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 Console:** http://localhost:8080/h2-console

---

## 📚 Documentação

Toda documentação está organizada na pasta `docs/`:

### 📖 Comece Aqui
- [**00_COMECE_AQUI.md**](docs/00_COMECE_AQUI.md) - Guia de início rápido (Português)
- [**START_HERE.md**](docs/START_HERE.md) - Quick start guide (English)

### 🏛️ Arquitetura e Padrões
- [**AGENTS.md**](docs/AGENTS.md) - Padrões do projeto e convenções
- [**REFACTORING_MANYTOMANY.md**](docs/REFACTORING_MANYTOMANY.md) - Detalhes da refatoração técnica

### 💻 Uso da API
- [**API_USAGE_EXAMPLES.md**](docs/API_USAGE_EXAMPLES.md) - Exemplos práticos de requisições
- [**VALIDATION_GUIDE.md**](docs/VALIDATION_GUIDE.md) - Guia completo de testes

### 📊 Relatórios
- [**REFACTORING_FINAL_REPORT.md**](docs/REFACTORING_FINAL_REPORT.md) - Sumário executivo
- [**REFACTORING_SUMMARY.md**](docs/REFACTORING_SUMMARY.md) - Checklist completo
- [**RELATORIO_GERAL_CONSOLIDADO.md**](docs/RELATORIO_GERAL_CONSOLIDADO.md) - Relatório consolidado

### 🧪 Testes
- [**TESTES_REFATORADOS.md**](docs/TESTES_REFATORADOS.md) - Mudanças nos testes
- [**EXEMPLOS_TESTES_NOVOS.md**](docs/EXEMPLOS_TESTES_NOVOS.md) - Código dos novos testes

### 📋 Índices
- [**DOCUMENTATION_INDEX.md**](docs/DOCUMENTATION_INDEX.md) - Índice completo
- [**DOCUMENTACAO_COMPLETA.md**](docs/DOCUMENTACAO_COMPLETA.md) - Documentação completa

---

## 🔄 Refatoração ManyToMany

Este projeto demonstra uma **refatoração completa** de relacionamento JPA:

### Antes (ManyToOne)
```java
// FuelPump.java
@ManyToOne
private FuelType fuelType;
```

### Depois (ManyToMany)
```java
// FuelPump.java
@ManyToMany
@JoinTable(name = "fuel_pump_fuel_type")
private Set<FuelType> fuelTypes;
```

### Mudanças Incluídas
- ✅ **11 arquivos** de código modificados
- ✅ **Regra de negócio** implementada (mínimo 1 combustível)
- ✅ **DTOs atualizados** para múltiplos IDs
- ✅ **Queries JPQL** corrigidas
- ✅ **Testes refatorados** (9 testes unitários)
- ✅ **Migração Flyway** V3 criada
- ✅ **Documentação completa** (~2000 linhas)

---

## 🧪 Testes

```bash
# Executar todos os testes
gradlew.bat test

# Executar testes específicos
gradlew.bat test --tests FuelPumpServiceTest

# Com relatório de cobertura
gradlew.bat jacocoTestReport
```

### Cobertura de Testes
- **9 testes unitários** no FuelPumpService
- **Validação completa** da regra de negócio
- **Mocks corretos** sem "Unnecessary stubbings"

---

## 🗄️ Banco de Dados

### Migrações Flyway
- **V1:** Schema inicial
- **V2:** Dados de exemplo
- **V3:** Refatoração ManyToMany (nova tabela de junção)

### Configuração H2
- **URL:** `jdbc:h2:file:./data/fuelstation`
- **Console:** http://localhost:8080/h2-console
- **User:** sa
- **Password:** (vazio)

---

## 📦 Build e Dependências

### Gradle Tasks Principais
```bash
gradlew.bat clean          # Limpar build
gradlew.bat compile        # Compilar código
gradlew.bat test           # Executar testes
gradlew.bat bootRun        # Executar aplicação
gradlew.bat bootJar        # Criar JAR executável
```

### Dependências Principais
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- H2 Database
- Flyway Migration
- MapStruct
- Lombok
- JUnit 5 + Mockito

---

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

### Padrões de Código
- Consulte [**AGENTS.md**](docs/AGENTS.md) para convenções
- Use checkstyle: `gradlew.bat checkstyleMain`

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 📞 Suporte

Para dúvidas ou sugestões:

1. **Leia a documentação** na pasta `docs/`
2. **Consulte os exemplos** em `API_USAGE_EXAMPLES.md`
3. **Verifique os testes** em `VALIDATION_GUIDE.md`

### Contato
- **Issues:** Use o GitHub Issues
- **Discussions:** GitHub Discussions

---

## 🎯 Destaques do Projeto

- ✅ **Refatoração Completa** ManyToOne → ManyToMany
- ✅ **Regra de Negócio** implementada e validada
- ✅ **Documentação Extensiva** (2000+ linhas)
- ✅ **Testes Completos** sem redundâncias
- ✅ **Código Limpo** seguindo padrões
- ✅ **Produção Ready** com migrações automáticas

---

**⭐ Se este projeto foi útil, dê uma estrela no GitHub!**

---

*Desenvolvido com ❤️ usando GitHub Copilot*
