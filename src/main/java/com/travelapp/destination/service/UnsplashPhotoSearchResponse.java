package com.travelapp.destination.service;

import java.util.List;

public record UnsplashPhotoSearchResponse(
        List<UnsplashPhotoResult> results
) {
}
