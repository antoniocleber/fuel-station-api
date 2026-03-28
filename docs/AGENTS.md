# Diretrizes para Agentes de Codificação IA para API Fuel Station

## Visão Geral da Arquitetura
Esta é uma API REST Spring Boot 3.5 seguindo arquitetura em camadas:
- **Controller** → **Service** → **Repository** → **Banco de Dados H2**
- Usa DTOs para separação de requisição/resposta
- MapStruct para mapeamento type-safe entidade ↔ DTO
- Flyway para migrações versionadas de banco de dados

## Padrões e Convenções Principais

### Interações de Camadas
- Controllers delegam para Services; nunca chamam Repositories diretamente
- Services lidam com lógica de negócio, transações e orquestram chamadas
- Repositories estendem `JpaRepository` com métodos de consulta customizados
- Mappers convertem entre entidades e DTOs usando MapStruct

### Gerenciamento de Transações
- Operações de leitura: `@Transactional(readOnly = true)` para otimização
- Operações de escrita: `@Transactional` (propagação padrão)
- Exemplo: `fuelTypeService.findAll()` usa transação readOnly

### Relacionamentos de Entidades e Consultas
- Use `JOIN FETCH` em consultas JPQL para evitar problemas N+1
- Exemplo: `SELECT fp FROM FuelPump fp JOIN FETCH fp.fuelTypes`
- Carregamento lazy por padrão; carregue eagerly apenas quando necessário
- **ManyToMany (FuelPump ↔ FuelType)**: Uma bomba pode ter múltiplos combustíveis
  - Tabela de junção: `fuel_pump_fuel_type` com `ON DELETE CASCADE` para bombas
  - Use `Set<FuelType>` na entidade para evitar duplicatas
  - Sempre validar que coleção não está vazia na criação/atualização
  - Usar `DISTINCT` em queries para evitar duplicatas quando faz JOIN em coleções

### Validação e Tratamento de Erros
- Use `@Valid` em métodos de controller para validação automática
- Exceções customizadas: `ResourceNotFoundException`, `ConflictException`, `BusinessException`
- Handler global de exceções padroniza respostas de erro
- Regras de negócio aplicadas em Services (ex.: validação de nome único)

### Mapeamento e DTOs
- MapStruct gera mappers em tempo de compilação
- Ignore campos gerenciados por JPA em mapeamentos (id, timestamps, relações)
- Use `NullValuePropertyMappingStrategy.IGNORE` para atualizações tipo PATCH
- Exemplo: `fuelTypeMapper.updateEntityFromRequest(request, entity)`

### Logging
- Use `@Slf4j` para logging
- Níveis de log: DEBUG para operações detalhadas, INFO para criações/atualizações, WARN para erros
- Exemplo: `log.info("Tipo de combustível criado com id={}", saved.getId())`

### Convenções de Nomenclatura
- Classes: PascalCase (FuelTypeController, FuelTypeService)
- Pacotes: lowercase (controller, service, repository)
- Endpoints: `/api/v1/{resource}` (ex.: `/api/v1/fuel-types`)
- Banco de dados: snake_case colunas (fuel_type, price_per_liter)

## Fluxo de Desenvolvimento

### Executando a Aplicação
```bash
# Windows
gradlew.bat bootRun

# Linux/macOS
./gradlew bootRun
```
- Inicia em http://localhost:8080
- Console H2: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:file:./data/fuelstation`, usuário: sa, senha: vazia)
- Swagger UI: http://localhost:8080/swagger-ui.html

### Testes e Verificações de Qualidade
```bash
# Executar todos os testes
./gradlew test

# Gerar relatório de cobertura (JaCoCo)
./gradlew jacocoTestReport

# Verificar estilo de código (Checkstyle)
./gradlew checkstyleMain

# Executar todas as verificações
./gradlew check
```
- Cobertura mínima de 70% de testes aplicada
- Relatórios em `build/reports/` directory

### Gerenciamento de Banco de Dados
- Schema gerenciado por migrações Flyway em `src/main/resources/db/migration/`
- Dados persistem em `./data/fuelstation.mv.db` entre reinicializações
- Use console H2 para acesso direto ao banco de dados/consultas

## Exemplos de Estrutura de Código

### Padrão de Controller
```java
@RestController
@RequestMapping("/api/v1/fuel-types")
@RequiredArgsConstructor
@Tag(name = "Fuel Types")
public class FuelTypeController {
    private final FuelTypeService fuelTypeService;

    @GetMapping
    public ResponseEntity<List<FuelTypeResponse>> findAll() {
        return ResponseEntity.ok(fuelTypeService.findAll());
    }

    @PostMapping
    public ResponseEntity<FuelTypeResponse> create(@Valid @RequestBody FuelTypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fuelTypeService.create(request));
    }
}
```

### Padrão de Service
```java
@Slf4j
@Service
@RequiredArgsConstructor
public class FuelTypeService {
    private final FuelTypeRepository fuelTypeRepository;
    private final FuelTypeMapper fuelTypeMapper;

    @Transactional(readOnly = true)
    public List<FuelTypeResponse> findAll() {
        return fuelTypeMapper.toResponseList(fuelTypeRepository.findAll());
    }

    @Transactional
    public FuelTypeResponse create(FuelTypeRequest request) {
        validateUniqueName(request.getName(), null);
        FuelType entity = fuelTypeMapper.toEntity(request);
        FuelType saved = fuelTypeRepository.save(entity);
        return fuelTypeMapper.toResponse(saved);
    }
}
```

### Padrão de Entity
```java
@Entity
@Table(name = "fuel_type")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "pumps")
public class FuelType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "price_per_liter", nullable = false, precision = 10, scale = 3)
    private BigDecimal pricePerLiter;

    @ManyToMany(mappedBy = "fuelTypes", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<FuelPump> pumps = new HashSet<>();

    @PrePersist @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}
```

### Exemplo de ManyToMany (FuelPump ↔ FuelType)
```java
@Entity
@Table(name = "fuel_pump")
public class FuelPump {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // Uma bomba pode ter múltiplos tipos de combustível
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "fuel_pump_fuel_type",
        joinColumns = @JoinColumn(name = "fuel_pump_id"),
        inverseJoinColumns = @JoinColumn(name = "fuel_type_id")
    )
    @Builder.Default
    private Set<FuelType> fuelTypes = new HashSet<>();
}
```

### Padrão de Repository
```java
@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, Long id);
}
```

## Regras de Negócio
- Nomes de tipos de combustível devem ser únicos
- Nomes de bombas de combustível devem ser únicos
- **Uma bomba deve ter pelo menos um tipo de combustível associado** (ManyToMany)
- Uma bomba pode ter múltiplos tipos de combustível cadastrados simultaneamente
- Não é possível excluir tipos de combustível com bombas associadas (ON DELETE RESTRICT)
- Não é possível excluir bombas de combustível com registros de abastecimento
- Datas de abastecimento não podem ser no futuro
- Litros e valor total devem ser > 0

## Dependências e Configuração
- Java 21, Spring Boot 3.5 BOM
- Gradle Kotlin DSL para scripts de build
- Lombok para redução de boilerplate
- MapStruct para mapeamento DTO
- H2 para banco de dados embarcado
- Flyway para migrações
- SpringDoc OpenAPI para documentação
- Checkstyle e JaCoCo para portões de qualidade
