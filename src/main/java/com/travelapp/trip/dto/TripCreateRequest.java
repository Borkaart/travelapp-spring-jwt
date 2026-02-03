package com.travelapp.trip.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TripCreateRequest {
    @NotBlank
    private String title;

    @NotNull
    private Long destinationId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}

