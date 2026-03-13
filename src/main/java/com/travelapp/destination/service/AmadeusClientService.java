package com.travelapp.destination.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(AmadeusProperties.class)
public class AmadeusClientService {

    private static final Logger logger = LoggerFactory.getLogger(AmadeusClientService.class);

    private final AmadeusProperties amadeusProperties;

    public String fetchAccessToken() {
        try {
            logger.debug("Fetching Amadeus access token with clientId: {}", amadeusProperties.clientId());
            
            LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "client_credentials");
            body.add("client_id", amadeusProperties.clientId().trim());
            body.add("client_secret", amadeusProperties.clientSecret().trim());

            AmadeusTokenResponse response = restClient().post()
                    .uri("/v1/security/oauth2/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(AmadeusTokenResponse.class);

            if (response != null && response.accessToken() != null) {
                logger.debug("Successfully obtained Amadeus access token");
                return response.accessToken();
            } else {
                logger.error("Failed to obtain Amadeus access token: Response or token is null");
                return null;
            }
        } catch (Exception ex) {
            logger.error("Error fetching Amadeus access token", ex);
            return null;
        }
    }

    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(resolveBaseUrl())
                .build();
    }

    public boolean isConfigured() {
        boolean configured = amadeusProperties.enabled()
                && StringUtils.hasText(amadeusProperties.clientId())
                && StringUtils.hasText(amadeusProperties.clientSecret());
        
        if (!configured) {
            logger.warn("Amadeus is NOT configured: enabled={}, clientId={}, clientSecret={}", 
                amadeusProperties.enabled(), 
                StringUtils.hasText(amadeusProperties.clientId()), 
                StringUtils.hasText(amadeusProperties.clientSecret()));
        }
        return configured;
    }

    private String resolveBaseUrl() {
        if (StringUtils.hasText(amadeusProperties.baseUrl())) {
            return amadeusProperties.baseUrl().trim();
        }
        return "https://test.api.amadeus.com";
    }
}
