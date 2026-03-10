package com.travelapp.destination.service;

import lombok.RequiredArgsConstructor;
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

    private final AmadeusProperties amadeusProperties;

    public String fetchAccessToken() {
        try {
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

            return response != null ? response.accessToken() : null;
        } catch (RuntimeException ex) {
            return null;
        }
    }

    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(resolveBaseUrl())
                .build();
    }

    public boolean isConfigured() {
        return amadeusProperties.enabled()
                && StringUtils.hasText(amadeusProperties.clientId())
                && StringUtils.hasText(amadeusProperties.clientSecret());
    }

    private String resolveBaseUrl() {
        if (StringUtils.hasText(amadeusProperties.baseUrl())) {
            return amadeusProperties.baseUrl().trim();
        }
        return "https://test.api.amadeus.com";
    }
}
