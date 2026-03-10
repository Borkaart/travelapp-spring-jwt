package com.travelapp.trip.service;

import com.travelapp.entity.User;
import com.travelapp.trip.domain.Trip;
import com.travelapp.trip.repository.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripAccessService {

    private final TripRepository tripRepository;

    public Trip getOwnedTrip(Long tripId, User user) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        Long ownerId = trip.getOwner() != null ? trip.getOwner().getId() : null;
        Long userId = user != null ? user.getId() : null;

        if (ownerId == null || userId == null || !ownerId.equals(userId)) {
            throw new AccessDeniedException("Not your trip");
        }

        return trip;
    }
}
