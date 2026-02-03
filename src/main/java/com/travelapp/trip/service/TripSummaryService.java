package com.travelapp.trip.service;

import com.travelapp.entity.User;
import com.travelapp.trip.repository.TripRepository;
import com.travelapp.trip.repository.TripSummaryProjection;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TripSummaryService {

    private final TripRepository tripRepository;

    @Transactional(readOnly = true)
    public TripSummaryProjection getSummary(Long tripId, User user) {

        if (!tripRepository.existsById(tripId)) {
            throw new EntityNotFoundException("Trip not found");
        }

        if (!tripRepository.existsByIdAndOwnerId(tripId, user.getId())) {
            throw new AccessDeniedException("Not your trip");
        }

        return tripRepository.findTripSummary(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip summary not found"));
    }
}
