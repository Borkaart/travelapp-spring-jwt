package com.travelapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "message", "Application is running");
    }

    @GetMapping("/")
    public String root() {
        return "Travel App Backend is running!";
    }
}
