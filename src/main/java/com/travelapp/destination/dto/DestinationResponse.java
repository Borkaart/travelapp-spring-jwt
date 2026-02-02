package com.travelapp.destination.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DestinationResponse {
    private Long id;
    private String name;
    private String country;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
}
