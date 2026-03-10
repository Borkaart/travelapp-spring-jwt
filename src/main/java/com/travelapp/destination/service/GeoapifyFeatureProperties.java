package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GeoapifyFeatureProperties(
        String city,
        String country,
        String formatted,
        Double lat,
        Double lon,
        String name,
        List<String> categories,
        @JsonProperty("place_id")
        String placeId,
        @JsonProperty("country_code")
        String countryCode
) {
}
