package com.travelapp.destination.service;

import java.util.List;

public record GeoapifyFeatureCollection(
        List<GeoapifyFeature> features
) {
}
