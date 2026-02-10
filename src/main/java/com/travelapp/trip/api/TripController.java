package com.travelapp.trip.api;


import com.travelapp.entity.User;
import com.travelapp.trip.dto.TripCreateRequest;
import com.travelapp.trip.dto.TripResponse;
import com.travelapp.trip.repository.TripSummaryProjection;
import com.travelapp.trip.service.TripService;
import com.travelapp.trip.service.TripSummaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private static final Set<String> ALLOWED_SORTS = Set.of(
            "id", "name", "startDate", "endDate", "createdAt"
    );

    private final TripService tripService;
    private final TripSummaryService tripSummaryService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TripResponse create(
            @RequestBody @Valid TripCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return tripService.create(request, user);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Page<TripResponse> myTrips(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Pageable safePageable = sanitizePageable(pageable);
        return tripService.listMyTrips(user, safePageable);
    }

    private Pageable sanitizePageable(Pageable pageable) {
        Sort safeSort = Sort.unsorted();

        if (pageable.getSort() != null && pageable.getSort().isSorted()) {
            Sort result = Sort.unsorted();
            for (Sort.Order order : pageable.getSort()) {
                String prop = order.getProperty();
                if (ALLOWED_SORTS.contains(prop)) {
                    result = result.and(Sort.by(new Sort.Order(order.getDirection(), prop)));
                }
            }
            safeSort = result.isSorted() ? result : Sort.by(Sort.Order.desc("id"));
        } else {
            safeSort = Sort.by(Sort.Order.desc("id"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), safeSort);
    }

    @GetMapping("/{tripId}/summary")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public TripSummaryProjection summary(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User user
    ) {
        return tripSummaryService.getSummary(tripId, user);
    }
}
