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

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Primary;
import javax.sql.DataSource;

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
    @Primary
    public DataSource dataSource() {
        String finalUrl;
        String username;
        String password;

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            try {
                logger.info("Configuring PRIMARY DataSource from DATABASE_URL");
                
                String cleanUrl = databaseUrl.trim();
                if (cleanUrl.startsWith("postgres://")) {
                    cleanUrl = cleanUrl.replaceFirst("postgres://", "postgresql://");
                }
                
                URI dbUri = new URI(cleanUrl);
                
                username = "";
                password = "";
                String userInfo = dbUri.getUserInfo();
                if (userInfo != null && userInfo.contains(":")) {
                    username = userInfo.split(":")[0];
                    password = userInfo.split(":")[1];
                }
                
                finalUrl = String.format("jdbc:postgresql://%s:%d%s", 
                        dbUri.getHost(), dbUri.getPort(), dbUri.getPath());
                
                logger.info("JDBC URL constructed successfully: {}", finalUrl);
                
            } catch (URISyntaxException | ArrayIndexOutOfBoundsException e) {
                logger.error("Critical error parsing DATABASE_URL: {}. Falling back to component variables.", e.getMessage());
                return buildFromComponents();
            }
        } else {
            logger.info("DATABASE_URL not found. Using component variables (DB_HOST, etc).");
            return buildFromComponents();
        }

        return buildDataSource(finalUrl, username, password);
    }

    private DataSource buildFromComponents() {
        String host = environment.getProperty("DB_HOST", "localhost");
        String port = environment.getProperty("DB_PORT", "5432");
        String name = environment.getProperty("DB_NAME", "travelapp");
        String username = environment.getProperty("DB_USER", "postgres");
        String password = environment.getProperty("DB_PASSWORD", "postgres");
        
        String finalUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, name);
        
        if (host.equals("localhost") && environment.matchesProfiles("prod")) {
            logger.error("CRITICAL: Running in PROD but no database configuration found (DB_HOST is null)!");
        }
        
        return buildDataSource(finalUrl, username, password);
    }

    private DataSource buildDataSource(String url, String username, String password) {
        logger.info("Building DataSource for URL: {}", url);
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}

