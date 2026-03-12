package com.travelapp.destination.service;

import java.util.List;

public record AmadeusLocation(
        String name,
        String subType, // CITY, POINT_OF_INTEREST
        String category, // SIGHTS, etc. (for POIs)
        List<String> tags,
        AmadeusAddress address,
        AmadeusGeoCode geoCode
) {}
