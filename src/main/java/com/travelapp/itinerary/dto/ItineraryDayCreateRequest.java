package com.travelapp.itinerary.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ItineraryDayCreateRequest {

    @NotNull
    private Long tripId;

    @NotNull
    private LocalDate date;
}
