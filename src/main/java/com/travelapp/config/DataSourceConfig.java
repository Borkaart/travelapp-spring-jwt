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
            logger.info("DATABASE_URL found. Converting to JDBC URL format.");
            // No Railway, a DATABASE_URL já contém user/pass. 
            // Basta trocar o protocolo para jdbc:postgresql://
            String jdbcUrl = databaseUrl.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
            properties.setUrl(jdbcUrl);
            logger.info("JDBC URL set to: {}", properties.getUrl());
        } else {
            logger.info("DATABASE_URL not found. Using application.yml or defaults.");
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
        return properties;
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}

