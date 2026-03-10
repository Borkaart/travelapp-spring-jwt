package com.travelapp.destination.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeoapifyPlaceDetailProperties(
        String website,
        GeoapifyWikiAndMedia wikiAndMedia
) {
    public record GeoapifyWikiAndMedia(
            @JsonProperty("image")
            String image
    ) {
    }
}
