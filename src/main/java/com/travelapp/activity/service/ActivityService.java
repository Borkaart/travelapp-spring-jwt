package com.travelapp.activity.service;

import com.travelapp.activity.domain.Activity;
import com.travelapp.activity.dto.ActivityCreateRequest;
import com.travelapp.activity.dto.ActivityResponse;
import com.travelapp.activity.dto.ActivityUpdateRequest;
import com.travelapp.activity.repository.ActivityRepository;
import com.travelapp.entity.User;
import com.travelapp.exception.InvalidActivityCostException;
import com.travelapp.itinerary.domain.ItineraryDay;
import com.travelapp.itinerary.repository.ItineraryDayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final ItineraryDayRepository itineraryDayRepository;

    @Transactional
    public ActivityResponse create(ActivityCreateRequest request, User user) {

        ItineraryDay day = itineraryDayRepository.findOwnedById(request.getItineraryDayId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Itinerary day not found"));

        validateCost(request.getCost());

        Activity activity = Activity.builder()
                .itineraryDay(day)
                .type(request.getType())
                .title(request.getTitle())
                .place(request.getPlace())
                .notes(request.getNotes())
                .time(request.getTime())
                .cost(request.getCost())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(activityRepository.save(activity));
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> listByItineraryDay(Long itineraryDayId, User user) {

        itineraryDayRepository.findOwnedById(itineraryDayId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Itinerary day not found"));

        return activityRepository
                .findAllByItineraryDayIdOrderByTimeAscCreatedAtAsc(itineraryDayId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ActivityResponse update(Long activityId, ActivityUpdateRequest request, User user) {

        Activity activity = activityRepository.findOwnedById(activityId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        // ownership já garantido pela query, mas pode manter a checagem se quiser redundância:
        // assertTripOwner(activity.getItineraryDay().getTrip().getOwner().getId(), user);

        validateCost(request.getCost());

        // PATCH-like: só atualiza o que veio
        if (request.getType() != null) activity.setType(request.getType());
        if (request.getTitle() != null) activity.setTitle(request.getTitle());
        if (request.getPlace() != null) activity.setPlace(request.getPlace());
        if (request.getNotes() != null) activity.setNotes(request.getNotes());
        if (request.getTime() != null) activity.setTime(request.getTime());
        if (request.getCost() != null) activity.setCost(request.getCost());

        return toResponse(activityRepository.save(activity));
    }

    @Transactional
    public void delete(Long activityId, User user) {

        Activity activity = activityRepository.findOwnedById(activityId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        activityRepository.delete(activity);
    }

    private void assertTripOwner(Long ownerId, User user) {
        if (!ownerId.equals(user.getId())) {
            throw new AccessDeniedException("Not your trip");
        }
    }

    private void validateCost(java.math.BigDecimal cost) {
        if (cost != null && cost.signum() < 0) {
            throw new InvalidActivityCostException();
        }
    }

    private ActivityResponse toResponse(Activity a) {
        return ActivityResponse.builder()
                .id(a.getId())
                .itineraryDayId(a.getItineraryDay().getId())
                .type(a.getType())
                .title(a.getTitle())
                .place(a.getPlace())
                .notes(a.getNotes())
                .time(a.getTime())
                .cost(a.getCost())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
