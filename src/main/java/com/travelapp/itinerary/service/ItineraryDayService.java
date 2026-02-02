package com.travelapp.itinerary.service;

import com.travelapp.entity.User;
import com.travelapp.exception.DuplicateResourceException;
import com.travelapp.exception.InvalidTripDateRangeException;
import com.travelapp.itinerary.domain.ItineraryDay;
import com.travelapp.itinerary.dto.ItineraryDayCreateRequest;
import com.travelapp.itinerary.dto.ItineraryDayResponse;
import com.travelapp.itinerary.repository.ItineraryDayRepository;
import com.travelapp.trip.domain.Trip;
import com.travelapp.trip.repository.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItineraryDayService {

    private final ItineraryDayRepository itineraryDayRepository;
    private final TripRepository tripRepository;

    public ItineraryDayResponse create(ItineraryDayCreateRequest request, User user) {

        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        // ownership
        if (!trip.getOwner().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Not your trip");
        }

        // dia dentro do range
        if (request.getDate().isBefore(trip.getStartDate()) || request.getDate().isAfter(trip.getEndDate())) {
            throw new InvalidTripDateRangeException("Day must be within trip date range");
        }

        if (itineraryDayRepository.existsByTripIdAndDate(trip.getId(), request.getDate())) {
            throw new DuplicateResourceException("Itinerary day already exists for this date");
        }


        ItineraryDay day = ItineraryDay.builder()
                .trip(trip)
                .date(request.getDate())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(itineraryDayRepository.save(day));
    }

    public List<ItineraryDayResponse> listByTrip(Long tripId, User user) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        if (!trip.getOwner().getId().equals(user.getId())) {
            throw new org.springframework.security.access.AccessDeniedException("Not your trip");
        }

        return itineraryDayRepository.findAllByTripIdOrderByDateAsc(tripId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ItineraryDayResponse toResponse(ItineraryDay day) {
        return ItineraryDayResponse.builder()
                .id(day.getId())
                .tripId(day.getTrip().getId())
                .date(day.getDate())
                .createdAt(day.getCreatedAt())
                .build();
    }
}
