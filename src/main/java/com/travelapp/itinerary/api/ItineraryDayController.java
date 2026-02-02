package com.travelapp.itinerary.api;

import com.travelapp.entity.User;
import com.travelapp.itinerary.dto.ItineraryDayCreateRequest;
import com.travelapp.itinerary.dto.ItineraryDayResponse;
import com.travelapp.itinerary.service.ItineraryDayService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary-days")
@RequiredArgsConstructor
public class ItineraryDayController {

    private final ItineraryDayService itineraryDayService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ItineraryDayResponse create(
            @RequestBody @Valid ItineraryDayCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return itineraryDayService.create(request, user);
    }

    @GetMapping("/trip/{tripId}")
    @PreAuthorize("hasRole('USER')")
    public List<ItineraryDayResponse> listByTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User user
    ) {
        return itineraryDayService.listByTrip(tripId, user);
    }
}
