package com.travelapp.config;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
public class DatabaseMigrationConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseMigrationConfig.class);
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public CommandLineRunner migrateDatabase() {
        return args -> {
            logger.info("Starting manual database migration repair...");
            try {
                // Reparando a tabela 'users'
                logger.info("Synchronizing columns for table: users");
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS is_profile_completed BOOLEAN DEFAULT FALSE");
                jdbcTemplate.execute("ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_image VARCHAR(255)");

                // Reparando a tabela 'user_profiles'
                logger.info("Synchronizing columns for table: user_profiles");
                jdbcTemplate.execute("ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS gender VARCHAR(50)");
                jdbcTemplate.execute("ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS theme_preference VARCHAR(50) DEFAULT 'SYSTEM'");
                jdbcTemplate.execute("ALTER TABLE user_profiles ADD COLUMN IF NOT EXISTS notifications_enabled BOOLEAN DEFAULT TRUE");
                
                // Garantindo que nomes de colunas CamelCase antigos (se existirem) não conflitem
                // O Hibernate agora vai buscar os nomes com underscore que definimos no @Column
                
                logger.info("Database migration repair completed successfully.");
            } catch (Exception e) {
                logger.error("Database migration repair failed: {}", e.getMessage());
                // Não travamos o boot, mas o 401 pode continuar se falhar aqui.
            }
        };
    }
}
