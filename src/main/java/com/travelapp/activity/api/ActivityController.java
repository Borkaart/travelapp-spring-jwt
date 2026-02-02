package com.travelapp.activity.api;

import com.travelapp.activity.dto.ActivityCreateRequest;
import com.travelapp.activity.dto.ActivityResponse;
import com.travelapp.activity.service.ActivityService;
import com.travelapp.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ActivityResponse create(
            @RequestBody @Valid ActivityCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return activityService.create(request, user);
    }

    @GetMapping("/day/{itineraryDayId}")
    @PreAuthorize("hasRole('USER')")
    public List<ActivityResponse> listByDay(
            @PathVariable Long itineraryDayId,
            @AuthenticationPrincipal User user
    ) {
        return activityService.listByItineraryDay(itineraryDayId, user);
    }
}
