package com.travelapp.itinerary.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ItineraryDayResponse {
    private Long id;
    private Long tripId;
    private LocalDate date;
    private LocalDateTime createdAt;
}
