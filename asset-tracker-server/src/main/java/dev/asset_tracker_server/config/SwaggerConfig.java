package dev.asset_tracker_server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Asset Tracker API")
                        .description("자산 트래커 서버 API 명세서 (Swagger UI)")
                        .version("v1.0.0"));
    }
} 