package com.travelapp.destination.service;

import com.travelapp.destination.domain.Destination;
import com.travelapp.destination.dto.DestinationPlaceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(GeoapifyProperties.class)
public class DestinationPlaceService {

    private static final String DEFAULT_CATEGORIES = "tourism.sights,tourism.attraction";
    private static final int DEFAULT_RADIUS_METERS = 10000;
    private static final int DEFAULT_LIMIT = 12;

    private final GeoapifyProperties geoapifyProperties;

    public List<DestinationPlaceResponse> getPlaces(Destination destination) {
        if (!geoapifyProperties.enabled() || !StringUtils.hasText(geoapifyProperties.apiKey())) {
            return Collections.emptyList();
        }

        GeoPoint geoPoint = geocodeDestination(destination);
        if (geoPoint == null) {
            return Collections.emptyList();
        }

        try {
            GeoapifyFeatureCollection response = restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/places")
                            .queryParam("categories", DEFAULT_CATEGORIES)
                            .queryParam("filter", "circle:" + geoPoint.lon() + "," + geoPoint.lat() + "," + DEFAULT_RADIUS_METERS)
                            .queryParam("bias", "proximity:" + geoPoint.lon() + "," + geoPoint.lat())
                            .queryParam("limit", DEFAULT_LIMIT)
                            .queryParam("apiKey", geoapifyProperties.apiKey().trim())
                            .build())
                    .retrieve()
                    .body(GeoapifyFeatureCollection.class);

            if (response == null || response.features() == null) {
                return Collections.emptyList();
            }

            return response.features().stream()
                    .map(feature -> toPlaceResponse(feature.properties()))
                    .filter(Objects::nonNull)
                    .toList();
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    private GeoPoint geocodeDestination(Destination destination) {
        try {
            GeoapifyFeatureCollection response = restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/geocode/search")
                            .queryParam("text", destination.getName() + ", " + destination.getCountry())
                            .queryParam("limit", 1)
                            .queryParam("apiKey", geoapifyProperties.apiKey().trim())
                            .build())
                    .retrieve()
                    .body(GeoapifyFeatureCollection.class);

            if (response == null || response.features() == null || response.features().isEmpty()) {
                return null;
            }

            GeoapifyFeatureProperties properties = response.features().get(0).properties();
            if (properties == null || properties.lat() == null || properties.lon() == null) {
                return null;
            }

            return new GeoPoint(properties.lat(), properties.lon());
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private DestinationPlaceResponse toPlaceResponse(GeoapifyFeatureProperties properties) {
        if (properties == null) {
            return null;
        }

        GeoapifyPlaceDetailProperties details = fetchPlaceDetails(properties.placeId());

        return DestinationPlaceResponse.builder()
                .name(resolveName(properties))
                .category(resolveCategory(properties))
                .formatted(properties.formatted())
                .website(details != null ? details.website() : null)
                .imageUrl(resolveImageUrl(details))
                .lat(properties.lat())
                .lon(properties.lon())
                .build();
    }

    private String resolveName(GeoapifyFeatureProperties properties) {
        if (StringUtils.hasText(properties.name())) {
            return properties.name();
        }
        if (StringUtils.hasText(properties.formatted())) {
            return properties.formatted();
        }
        return "Ponto de interesse";
    }

    private String resolveCategory(GeoapifyFeatureProperties properties) {
        if (properties.categories() == null || properties.categories().isEmpty()) {
            return null;
        }
        return properties.categories().stream()
                .filter(StringUtils::hasText)
                .findFirst()
                .orElse(null);
    }

    private GeoapifyPlaceDetailProperties fetchPlaceDetails(String placeId) {
        if (!StringUtils.hasText(placeId)) {
            return null;
        }

        try {
            GeoapifyPlaceDetailsResponse response = restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v2/place-details")
                            .queryParam("id", placeId)
                            .queryParam("features", "details")
                            .queryParam("apiKey", geoapifyProperties.apiKey().trim())
                            .build())
                    .retrieve()
                    .body(GeoapifyPlaceDetailsResponse.class);

            if (response == null || response.features() == null || response.features().isEmpty()) {
                return null;
            }

            return response.features().get(0).properties();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String resolveImageUrl(GeoapifyPlaceDetailProperties details) {
        if (details == null || details.wikiAndMedia() == null) {
            return null;
        }
        return details.wikiAndMedia().image();
    }

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(resolveBaseUrl())
                .build();
    }

    private String resolveBaseUrl() {
        if (StringUtils.hasText(geoapifyProperties.baseUrl())) {
            return geoapifyProperties.baseUrl().trim();
        }
        return "https://api.geoapify.com";
    }
}
