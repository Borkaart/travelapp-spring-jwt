package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmadeusAddress(
        String cityName,
        String cityCode,
        String countryName,
        String countryCode
) {}
