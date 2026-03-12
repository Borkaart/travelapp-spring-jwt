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
public class DestinationPlaceService {

    private static final int DEFAULT_RADIUS_KM = 10;
    private static final int DEFAULT_LIMIT = 12;

    private final AmadeusClientService amadeusClientService;
    private final DestinationLookupService destinationLookupService;

    public List<DestinationPlaceResponse> getPlaces(Destination destination) {
        if (!amadeusClientService.isConfigured()) {
            return Collections.emptyList();
        }

        GeoPoint geoPoint = destinationLookupService.findCityCoordinates(destination.getName(), destination.getCountry());
        if (geoPoint == null) {
            return Collections.emptyList();
        }

        String token = amadeusClientService.fetchAccessToken();
        if (!StringUtils.hasText(token)) {
            return Collections.emptyList();
        }

        try {
            AmadeusLocationResponse response = amadeusClientService.restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/reference-data/locations/pois")
                            .queryParam("latitude", geoPoint.lat())
                            .queryParam("longitude", geoPoint.lon())
                            .queryParam("radius", DEFAULT_RADIUS_KM)
                            .queryParam("page[limit]", DEFAULT_LIMIT)
                            .queryParam("categories", "SIGHTS,HISTORICAL,BEACH_PARK,NIGHTLIFE")
                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(AmadeusLocationResponse.class);

            if (response == null || response.data() == null) {
                return Collections.emptyList();
            }

            return response.data().stream()
                    .map(this::toPlaceResponse)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    private DestinationPlaceResponse toPlaceResponse(AmadeusLocation location) {
        if (location == null) {
            return null;
        }

        return DestinationPlaceResponse.builder()
                .name(location.name())
                .category(location.category())
                .formatted(location.name()) // Amadeus POI often doesn't have a formatted address like Geoapify
                .website(null) // Amadeus POI doesn't typically return website in the basic response
                .imageUrl(null) // Amadeus POI doesn't return images usually
                .lat(location.geoCode() != null ? location.geoCode().latitude() : null)
                .lon(location.geoCode() != null ? location.geoCode().longitude() : null)
                .build();
    }
}
