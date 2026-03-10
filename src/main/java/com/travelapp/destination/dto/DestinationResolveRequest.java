package com.travelapp.destination.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DestinationResolveRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 120) String country
) {
}
