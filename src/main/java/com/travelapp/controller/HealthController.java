package com.travelapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.Arrays;

@RestController
public class HealthController {

    @Autowired
    private Environment environment;
    
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/api/health")
    public Map<String, Object> health() {
        boolean dbStatus = false;
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.execute("SELECT 1");
                dbStatus = true;
            }
        } catch (Exception e) {
            // Log error silently
        }

        return Map.of(
            "status", "UP",
            "profiles", Arrays.asList(environment.getActiveProfiles()),
            "database", dbStatus ? "CONNECTED" : "DISCONNECTED",
            "message", "Application is running"
        );
    }

    @GetMapping("/")
    public String root() {
        return "Travel App Backend is running!";
    }
}
