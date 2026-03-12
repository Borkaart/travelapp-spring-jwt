package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmadeusGeoCode(
        Double latitude,
        Double longitude
) {}
