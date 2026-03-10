package com.travelapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Converte postgres:// para jdbc:postgresql://
            String jdbcUrl = databaseUrl.replace("postgres://", "jdbc:postgresql://");
            properties.setUrl(jdbcUrl);
        } else {
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

