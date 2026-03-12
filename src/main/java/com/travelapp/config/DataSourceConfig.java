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
            // Converte postgres:// para jdbc:postgresql://
            String jdbcUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
            properties.setUrl(jdbcUrl);
        } else {
            if (environment.matchesProfiles("prod")) {
                throw new IllegalStateException("DATABASE_URL must be set when running with profile 'prod'");
            }
            properties.setUrl("jdbc:postgresql://localhost:5432/travelapp");
            properties.setUsername("postgres");
            properties.setPassword("postgres");
        }

        return properties;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}

