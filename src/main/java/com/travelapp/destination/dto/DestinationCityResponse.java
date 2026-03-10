package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationCityResponse {
    private String name;
    private String country;
    private String countryCode;
    private String formatted;
    private Double lat;
    private Double lon;
}
