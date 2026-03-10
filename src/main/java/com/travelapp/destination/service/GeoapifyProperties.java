package com.travelapp.destination.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "geoapify")
public record GeoapifyProperties(
        boolean enabled,
        String apiKey,
        String baseUrl
) {
}
