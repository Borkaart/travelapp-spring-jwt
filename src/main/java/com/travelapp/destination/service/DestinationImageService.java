package com.travelapp.destination.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(UnsplashProperties.class)
public class DestinationImageService {

    private final UnsplashProperties unsplashProperties;

    public String resolveImageUrl(String name, String country, String currentImageUrl) {
        if (StringUtils.hasText(currentImageUrl)) {
            return currentImageUrl.trim();
        }

        if (!unsplashProperties.enabled() || !StringUtils.hasText(unsplashProperties.accessKey())) {
            return null;
        }

        try {
            RestClient restClient = RestClient.builder()
                    .baseUrl(resolveBaseUrl())
                    .defaultHeader("Accept-Version", "v1")
                    .defaultHeader("Authorization", "Client-ID " + unsplashProperties.accessKey().trim())
                    .build();

            UnsplashPhotoSearchResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/photos")
                            .queryParam("query", buildQuery(name, country))
                            .queryParam("orientation", "landscape")
                            .queryParam("per_page", 1)
                            .build())
                    .retrieve()
                    .body(UnsplashPhotoSearchResponse.class);

            if (response == null || response.results() == null || response.results().isEmpty()) {
                return null;
            }

            UnsplashPhotoResult firstResult = response.results().get(0);
            if (firstResult == null || firstResult.urls() == null) {
                return null;
            }

            return firstResult.urls().regular();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String buildQuery(String name, String country) {
        if (StringUtils.hasText(country)) {
            return name + ", " + country;
        }
        return name;
    }

    private String resolveBaseUrl() {
        if (StringUtils.hasText(unsplashProperties.baseUrl())) {
            return unsplashProperties.baseUrl().trim();
        }
        return "https://api.unsplash.com";
    }
}
