package com.travelapp.destination.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amadeus")
public record AmadeusProperties(
        boolean enabled,
        String clientId,
        String clientSecret,
        String baseUrl
) {
}
