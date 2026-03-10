package com.travelapp.destination.service;

import java.util.List;

public record GeoapifyPlaceDetailsResponse(
        List<GeoapifyPlaceDetailFeature> features
) {
}
