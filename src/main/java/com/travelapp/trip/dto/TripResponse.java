package com.travelapp.trip.dto;

import com.travelapp.trip.domain.TripStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TripResponse {

    private Long id;
    private String title;

    private Long destinationId;
    private String destinationName;
    private String destinationImageUrl;

    private TripStatus status;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime createdAt;
}
