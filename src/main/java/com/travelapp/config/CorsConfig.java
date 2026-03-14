package com.travelapp.config;

import com.travelapp.security.CorsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);
    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        List<String> origins = corsProperties.allowedOrigins().stream()
                .flatMap(s -> java.util.Arrays.stream(s.split(","))) // Split comma-separated values
                .map(String::trim)
                .map(s -> s.replace("`", "")) // Remove backticks se vierem na var
                .filter(org.springframework.util.StringUtils::hasText)
                .collect(Collectors.toList());
        
        logger.info("Configuring CORS with allowed origins: {}", origins);

        config.setAllowedOrigins(origins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        config.setAllowCredentials(true);
        config.setMaxAge(3600L); // Cache do preflight por 1 hora

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
