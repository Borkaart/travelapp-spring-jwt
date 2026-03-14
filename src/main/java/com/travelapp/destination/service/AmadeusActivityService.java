package com.travelapp.destination.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AmadeusActivityService {

    private static final Logger logger = LoggerFactory.getLogger(AmadeusActivityService.class);
    private static final int DEFAULT_RADIUS_KM = 5;

    private final AmadeusClientService amadeusClientService;

    public List<AmadeusActivityData> searchActivities(Double lat, Double lon, Integer radius) {
        if (!amadeusClientService.isConfigured()) {
            return Collections.emptyList();
        }

        String token = amadeusClientService.fetchAccessToken();
        if (!StringUtils.hasText(token)) {
            return Collections.emptyList();
        }

        int searchRadius = (radius != null) ? radius : DEFAULT_RADIUS_KM;

        try {
            logger.debug("Searching Amadeus activities at lat: {}, lon: {}, radius: {}", lat, lon, searchRadius);
            
            AmadeusActivityResponse response = amadeusClientService.restClient().get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/shopping/activities")
                            .queryParam("latitude", lat)
                            .queryParam("longitude", lon)
                            .queryParam("radius", searchRadius)
                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(AmadeusActivityResponse.class);

            if (response != null && response.data() != null) {
                return response.data();
            }
        } catch (Exception ex) {
            logger.error("Error searching Amadeus activities", ex);
        }

        return Collections.emptyList();
    }
}
