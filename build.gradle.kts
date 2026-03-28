import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    // Checkstyle: análise estática de estilo de código
    checkstyle
    // JaCoCo: cobertura de testes
    jacoco
}

// Dependency Management via BOM nativo do Gradle (sem io.spring.dependency-management)
// Prática recomendada para builds mais rápidos (Spring Boot 3.3+)
dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
}

group = "com.fuelstation"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // ── Spring Boot Starters ──────────────────────────────────────────────────
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // ── Banco de Dados ────────────────────────────────────────────────────────
    // H2 em runtime para persistência em arquivo (embedded, sem servidor externo)
    runtimeOnly("com.h2database:h2")

    // Flyway: migrations versionadas de banco de dados
    implementation("org.flywaydb:flyway-core")

    // ── Utilitários ───────────────────────────────────────────────────────────
    // Lombok: reduz boilerplate (getters, setters, builders, etc.)
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // MapStruct: mapeamento de DTOs ↔ Entidades em tempo de compilação (type-safe)
    // IMPORTANTE: processado APÓS Lombok (ordem importa)
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

    // ── Documentação da API ───────────────────────────────────────────────────
    // SpringDoc OpenAPI (Swagger UI): gera documentação interativa da API REST
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")

    // ── Desenvolvimento ───────────────────────────────────────────────────────
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ── Testes ────────────────────────────────────────────────────────────────
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

// ── Configuração de Testes ────────────────────────────────────────────────────
tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// ── Configuração do JaCoCo ────────────────────────────────────────────────────
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}

// Cobertura mínima de 70% (opcional — comentar se quiser desativar)
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.70".toBigDecimal()
            }
        }
    }
}

// ── Configuração do Checkstyle ────────────────────────────────────────────────
checkstyle {
    toolVersion = "10.21.4"
    configFile = file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

// ── Configuração do Compilador Java ──────────────────────────────────────────
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    // -parameters: necessário para Spring MVC resolver nomes de parâmetros
    options.compilerArgs.add("-parameters")
}

// ── Boot JAR: definir classe main explicitamente ───────────────────────────────
springBoot {
    mainClass = "com.fuelstation.FuelStationApplication"
    buildInfo() // Gera META-INF/build-info.properties (exposto pelo Actuator)
}
