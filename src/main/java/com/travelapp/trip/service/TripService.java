package com.travelapp.trip.service;

import com.travelapp.destination.domain.Destination;
import com.travelapp.destination.repository.DestinationRepository;
import com.travelapp.entity.User;
import com.travelapp.trip.domain.Trip;
import com.travelapp.trip.domain.TripStatus;
import com.travelapp.trip.dto.TripCreateRequest;
import com.travelapp.trip.dto.TripResponse;
import com.travelapp.trip.repository.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TripService {

    private final TripRepository tripRepository;
    private final DestinationRepository destinationRepository;

    public TripResponse create(TripCreateRequest request, User user) {

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Destination destination = destinationRepository.findById(request.getDestinationId())
                .orElseThrow(() -> new EntityNotFoundException("Destination not found"));

        Trip trip = Trip.builder()
                .title(request.getTitle())
                .destination(destination)
                .owner(user)
                .status(TripStatus.PLANNED)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(tripRepository.save(trip));
    }

    public Page<TripResponse> listMyTrips(User user, Pageable pageable) {
        return tripRepository
                .findAllByOwnerId(user.getId(), pageable)
                .map(this::toResponse);
    }

    /* ===== mapper ===== */

    private TripResponse toResponse(Trip trip) {
        return TripResponse.builder()
                .id(trip.getId())
                .title(trip.getTitle())
                .destinationId(trip.getDestination().getId())
                .destinationName(trip.getDestination().getName())
                .status(trip.getStatus())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .createdAt(trip.getCreatedAt())
                .build();
    }
}
