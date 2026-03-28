package com.fuelstation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração da documentação OpenAPI 3.0 via SpringDoc.
 *
 * <p>Acesse a UI em: <a href="http://localhost:8080/swagger-ui.html">
 * http://localhost:8080/swagger-ui.html</a></p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fuel Station API")
                        .version("1.0.0")
                        .description("""
                                API RESTful para gerenciamento de abastecimentos em posto de combustível.
                                
                                ## Recursos disponíveis
                                - **Tipos de Combustível** — Gasolina, Etanol, Diesel, GNV, etc.
                                - **Bombas de Combustível** — Bombas associadas a um tipo de combustível.
                                - **Abastecimentos** — Registros de abastecimento por bomba, data e volume.
                                
                                ## Documentação técnica
                                - JSON Schema: [/api-docs](/api-docs)
                                """)
                        .contact(new Contact()
                                .name("Fuel Station Dev")
                                .email("dev@fuelstation.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Ambiente de Desenvolvimento")
                ));
    }
}
