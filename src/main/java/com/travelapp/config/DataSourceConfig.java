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
            try {
                logger.info("Parsing DATABASE_URL for data source configuration");
                
                // Converte postgresql:// ou postgres:// para um formato que o URI entenda
                String cleanUrl = databaseUrl.trim();
                if (cleanUrl.startsWith("postgres://")) {
                    cleanUrl = cleanUrl.replaceFirst("postgres://", "postgresql://");
                }
                
                URI dbUri = new URI(cleanUrl);
                
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                
                // O JDBC URL não deve conter o userInfo
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", 
                        dbUri.getHost(), dbUri.getPort(), dbUri.getPath());
                
                logger.info("JDBC URL constructed: {}", jdbcUrl);
                logger.debug("Database username: {}", username);

                properties.setUrl(jdbcUrl);
                properties.setUsername(username);
                properties.setPassword(password);
                
            } catch (URISyntaxException | ArrayIndexOutOfBoundsException e) {
                logger.error("Error parsing DATABASE_URL: {}. Falling back to default config.", e.getMessage());
                setFallbackProperties(properties);
            }
        } else {
            logger.info("DATABASE_URL not found. Using fallback configuration.");
            setFallbackProperties(properties);
        }

        return properties;
    }

    private void setFallbackProperties(DataSourceProperties properties) {
        String yamlUrl = environment.getProperty("spring.datasource.url");
        if (yamlUrl != null) {
            properties.setUrl(yamlUrl);
            properties.setUsername(environment.getProperty("spring.datasource.username"));
            properties.setPassword(environment.getProperty("spring.datasource.password"));
        } else {
            if (environment.matchesProfiles("prod")) {
                throw new IllegalStateException("DATABASE_URL or spring.datasource.url must be set in prod profile");
            }
            properties.setUrl("jdbc:postgresql://localhost:5432/travelapp");
            properties.setUsername("postgres");
            properties.setPassword("postgres");
        }
    }

    @Bean
    public DataSource dataSource(DataSourceProperties dataSourceProperties) {
        logger.info("Initializing DataSource with URL: {}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
}

