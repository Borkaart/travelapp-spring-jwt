package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationPlaceResponse {
    private String name;
    private String category;
    private String formatted;
    private String website;
    private String imageUrl;
    private Double lat;
    private Double lon;
}
