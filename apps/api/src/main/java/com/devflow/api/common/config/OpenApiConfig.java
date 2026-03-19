package com.devflow.api.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI devflowOpenApi() {
        return new OpenAPI().info(new Info()
                .title("DevFlow API")
                .version("v1")
                .description("DevFlow modular monolith API for community product demo.")
                .contact(new Contact()
                        .name("DevFlow Team")
                        .url("https://github.com/")
                        .email("devflow@example.com")));
    }

    @Bean
    public GroupedOpenApi publicApiGroup() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/v1/**")
                .pathsToExclude("/api/v1/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApiGroup() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/v1/admin/**")
                .build();
    }
}
