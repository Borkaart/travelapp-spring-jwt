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
            try {
                // Verificamos se a coluna 'token' existe na tabela 'refresh_tokens'
                Integer exists = jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM information_schema.columns WHERE table_name = 'refresh_tokens' AND column_name = 'token'",
                    Integer.class
                );

                if (exists != null && exists > 0) {
                    logger.info("Detectada coluna legada 'token' na tabela 'refresh_tokens'. Removendo a restrição NOT NULL ou dropando a coluna...");
                    // Opção segura: Alterar para NULLABLE para não perder dados se houver, ou dropar se tiver certeza.
                    // Vamos dropar a coluna pois ela foi substituida por 'token_hash' e está causando erros.
                    jdbcTemplate.execute("ALTER TABLE refresh_tokens DROP COLUMN IF EXISTS token");
                    logger.info("Coluna 'token' removida com sucesso da tabela 'refresh_tokens'.");
                }
            } catch (Exception e) {
                logger.error("Erro ao tentar corrigir schema do banco de dados: {}", e.getMessage());
            }
        };
    }
}
