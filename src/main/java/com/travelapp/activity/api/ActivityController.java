package com.travelapp.activity.api;

import com.travelapp.activity.dto.ActivityCreateRequest;
import com.travelapp.activity.dto.ActivityResponse;
import com.travelapp.activity.dto.ActivityUpdateRequest;
import com.travelapp.activity.service.ActivityService;
import com.travelapp.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityResponse create(
            @RequestBody @Valid ActivityCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return activityService.create(request, user);
    }

    @GetMapping("/itinerary-day/{itineraryDayId}")
    public List<ActivityResponse> listByItineraryDay(
            @PathVariable Long itineraryDayId,
            @AuthenticationPrincipal User user
    ) {
        return activityService.listByItineraryDay(itineraryDayId, user);
    }

    @PutMapping("/{id}")
    public ActivityResponse update(
            @PathVariable Long id,
            @RequestBody @Valid ActivityUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return activityService.update(id, request, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User user
    ) {
        activityService.delete(id, user);
    }
}
