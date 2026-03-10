package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AmadeusHotelData(
        @JsonProperty("hotelId")
        String hotelId,
        String name,
        AmadeusGeoCode geoCode,
        AmadeusAddress address,
        AmadeusDistance distance,
        @JsonProperty("rating")
        Integer rating
) {
    public record AmadeusGeoCode(
            Double latitude,
            Double longitude
    ) {
    }

    public record AmadeusAddress(
            java.util.List<String> lines,
            String cityName,
            String countryCode
    ) {
    }

    public record AmadeusDistance(
            Double value,
            String unit
    ) {
    }
}
