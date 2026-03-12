package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AmadeusLocation(
        String name,
        String subType, // CITY, POINT_OF_INTEREST
        String category, // SIGHTS, etc. (for POIs)
        List<String> tags,
        AmadeusAddress address,
        AmadeusGeoCode geoCode
) {}
