package com.travelapp.trip.api;

import com.travelapp.entity.User;
import com.travelapp.trip.dto.TripCreateRequest;
import com.travelapp.trip.dto.TripResponse;
import com.travelapp.trip.repository.TripSummaryProjection;
import com.travelapp.trip.service.TripService;
import com.travelapp.trip.service.TripSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;
    private final TripSummaryService tripSummaryService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public TripResponse create(
            @RequestBody @Valid TripCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return tripService.create(request, user);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public Page<TripResponse> myTrips(
            @AuthenticationPrincipal User user,
            Pageable pageable
    ) {
        return tripService.listMyTrips(user, pageable);
    }

    @GetMapping("/{tripId}/summary")
    @PreAuthorize("hasRole('USER')")
    public TripSummaryProjection summary(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User user
    ) {
        return tripSummaryService.getSummary(tripId, user);
    }
}
