package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationHotelResponse {
    private String hotelId;
    private String name;
    private String address;
    private String city;
    private String countryCode;
    private Double latitude;
    private Double longitude;
    private Double distanceValue;
    private String distanceUnit;
    private Integer rating;
}
