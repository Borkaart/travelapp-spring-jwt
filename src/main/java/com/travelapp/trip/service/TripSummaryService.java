package com.travelapp.trip.service;

import com.travelapp.entity.User;
import com.travelapp.trip.repository.TripRepository;
import com.travelapp.trip.repository.TripSummaryProjection;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripSummaryService {

    private final TripAccessService tripAccessService;
    private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public TripSummaryProjection getSummary(Long tripId, User user) {
        tripAccessService.getOwnedTrip(tripId, user);

        return tripRepository.findTripSummary(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip summary not found"));
    }
}
