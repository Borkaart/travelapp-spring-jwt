package com.travelapp.destination.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DestinationCreateRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String country,
        @Size(max = 2000) String description,
        @Size(max = 500) String imageUrl
) {}
