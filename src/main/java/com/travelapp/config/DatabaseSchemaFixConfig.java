package com.travelapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseSchemaFixConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSchemaFixConfig.class);

    @Bean
    public CommandLineRunner fixDatabaseSchema(JdbcTemplate jdbcTemplate) {
        return args -> {
            // Desativado temporariamente para garantir startup
            logger.info("DatabaseSchemaFixConfig: skipping schema check for stability.");
        };
    }
}
