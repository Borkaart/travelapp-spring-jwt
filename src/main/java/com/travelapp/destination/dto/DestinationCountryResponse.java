package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DestinationCountryResponse {
    private String code;
    private String name;
}
