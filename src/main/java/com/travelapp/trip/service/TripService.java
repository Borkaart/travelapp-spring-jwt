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
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TripService {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id", "title", "status", "startDate", "endDate", "createdAt"
    );

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
        Pageable safePageable = sanitizePageable(pageable);

        return tripRepository
                .findAllByOwnerId(user.getId(), safePageable)
                .map(this::toResponse);
    }

    private Pageable sanitizePageable(Pageable pageable) {
        // default seguro
        Sort safeSort = Sort.by(Sort.Order.desc("createdAt"));

        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            Sort filtered = Sort.unsorted();

            for (Sort.Order order : pageable.getSort()) {
                String prop = order.getProperty();

                // Bloqueia "['string']" e qualquer propriedade não permitida
                if (prop != null && ALLOWED_SORTS.contains(prop)) {
                    filtered = filtered.and(Sort.by(new Sort.Order(order.getDirection(), prop)));
                }
            }

            if (filtered.isSorted()) {
                safeSort = filtered;
            }
        }

        int page = pageable != null ? pageable.getPageNumber() : 0;
        int size = pageable != null ? pageable.getPageSize() : 20;

        return PageRequest.of(page, size, safeSort);
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
