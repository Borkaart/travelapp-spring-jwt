package com.travelapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    private final Environment environment;

    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                logger.info("DATABASE_URL found. Extracting credentials for JDBC...");
                
                // Converte postgres:// para postgresql:// para o URI parser
                String cleanUrl = databaseUrl.trim();
                if (cleanUrl.startsWith("postgres://")) {
                    cleanUrl = cleanUrl.replaceFirst("postgres://", "postgresql://");
                }
                
                URI dbUri = new URI(cleanUrl);
                
                // Extrai as partes
                String userInfo = dbUri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    String username = userInfo.split(":")[0];
                    String password = userInfo.split(":")[1];
                    properties.setUsername(username);
                    properties.setPassword(password);
                }
                
                // Monta a URL JDBC sem as credenciais no meio (o driver exige assim)
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", 
                        dbUri.getHost(), dbUri.getPort(), dbUri.getPath());
                
                properties.setUrl(jdbcUrl);
                logger.info("JDBC Connection configured: {}", jdbcUrl);
                
            } catch (URISyntaxException | ArrayIndexOutOfBoundsException e) {
                logger.error("Failed to parse DATABASE_URL: {}. Falling back.", e.getMessage());
                applyFallback(properties);
            }
        } else {
            logger.info("DATABASE_URL not found. Using application.yml or defaults.");
            applyFallback(properties);
        }

        return properties;
    }

    private void applyFallback(DataSourceProperties properties) {
        String yamlUrl = environment.getProperty("spring.datasource.url");
        if (yamlUrl != null) {
            properties.setUrl(yamlUrl);
            properties.setUsername(environment.getProperty("spring.datasource.username"));
            properties.setPassword(environment.getProperty("spring.datasource.password"));
        } else {
            if (environment.matchesProfiles("prod")) {
                logger.warn("Running in PROD but no database configuration found!");
            }
            properties.setUrl("jdbc:postgresql://localhost:5432/travelapp");
            properties.setUsername("postgres");
            properties.setPassword("postgres");
        }
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}

