package dev.asset_tracker_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 개발 단계에서 모든 CORS를 허용하는 설정입니다.
 * 운영 환경에서는 반드시 도메인 제한을 적용하세요!
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 운영 시에는 도메인 제한 필요
                .allowedMethods("*");
    }
} 
