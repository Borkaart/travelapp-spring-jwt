package com.travelapp.destination.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AmadeusTripParserService {

    private static final Logger logger = LoggerFactory.getLogger(AmadeusTripParserService.class);
    private final AmadeusClientService amadeusClient;

    public TripParserResponse parseConfirmation(String base64Content) {
        if (!amadeusClient.isConfigured()) {
            logger.warn("Amadeus is not configured, skipping trip parser");
            return null;
        }

        String token = amadeusClient.fetchAccessToken();
        if (token == null) {
            logger.error("Could not fetch Amadeus access token");
            return null;
        }

        try {
            TripParserRequest request = new TripParserRequest(new TripParserRequest.Data(base64Content));

            return amadeusClient.restClient().post()
                    .uri("/v1/travel/trip-parser")
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(TripParserResponse.class);
        } catch (Exception ex) {
            logger.error("Error calling Amadeus Trip Parser API", ex);
            return null;
        }
    }
}
