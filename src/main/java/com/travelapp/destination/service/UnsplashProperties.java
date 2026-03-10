package com.travelapp.destination.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "unsplash")
public record UnsplashProperties(
        boolean enabled,
        String accessKey,
        String baseUrl
) {
}
