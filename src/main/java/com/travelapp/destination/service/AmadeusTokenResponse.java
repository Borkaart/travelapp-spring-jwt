package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmadeusTokenResponse(
        @JsonProperty("access_token")
        String accessToken
) {
}
