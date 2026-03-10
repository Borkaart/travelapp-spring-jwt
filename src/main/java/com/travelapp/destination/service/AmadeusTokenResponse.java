package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AmadeusTokenResponse(
        @JsonProperty("access_token")
        String accessToken
) {
}
