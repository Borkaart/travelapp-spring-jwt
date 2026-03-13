package com.travelapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

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
            // Suporta tanto postgresql:// quanto postgres:// sem corromper a URL
            String jdbcUrl;
            if (databaseUrl.startsWith("postgresql://")) {
                jdbcUrl = databaseUrl.replaceFirst("postgresql://", "jdbc:postgresql://");
            } else if (databaseUrl.startsWith("postgres://")) {
                jdbcUrl = databaseUrl.replaceFirst("postgres://", "jdbc:postgresql://");
            } else {
                jdbcUrl = databaseUrl;
            }
            properties.setUrl(jdbcUrl);
        } else {
            // Tenta obter as propriedades do application.yml/properties ou usa defaults
            String yamlUrl = environment.getProperty("spring.datasource.url");
            if (yamlUrl != null) {
                properties.setUrl(yamlUrl);
                properties.setUsername(environment.getProperty("spring.datasource.username"));
                properties.setPassword(environment.getProperty("spring.datasource.password"));
            } else {
                if (environment.matchesProfiles("prod")) {
                    throw new IllegalStateException("DATABASE_URL or spring.datasource.url must be set in prod");
                }
                properties.setUrl("jdbc:postgresql://localhost:5432/travelapp");
                properties.setUsername("postgres");
                properties.setPassword("postgres");
            }
        }

        return properties;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}

