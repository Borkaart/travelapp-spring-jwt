package com.travelapp.activity.service;

import com.travelapp.activity.domain.Activity;
import com.travelapp.activity.dto.ActivityCreateRequest;
import com.travelapp.activity.dto.ActivityReorderRequest;
import com.travelapp.activity.dto.ActivityResponse;
import com.travelapp.activity.dto.ActivityUpdateRequest;
import com.travelapp.activity.repository.ActivityRepository;
import com.travelapp.entity.User;
import com.travelapp.exception.InvalidActivityCostException;
import com.travelapp.itinerary.domain.ItineraryDay;
import com.travelapp.itinerary.repository.ItineraryDayRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                .sortOrder(nextSortOrder(day.getId()))
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(activityRepository.save(activity));
    }

    @Transactional
    public List<ActivityResponse> listByItineraryDay(Long itineraryDayId, User user) {

        itineraryDayRepository.findOwnedById(itineraryDayId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Itinerary day not found"));

        normalizeSortOrder(itineraryDayId);

        return activityRepository
                .findAllByItineraryDayIdOrderBySortOrderAscCreatedAtAsc(itineraryDayId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public List<ActivityResponse> reorder(ActivityReorderRequest request, User user) {
        itineraryDayRepository.findOwnedById(request.getItineraryDayId(), user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Itinerary day not found"));

        List<Activity> activities = activityRepository.findAllByItineraryDayId(request.getItineraryDayId());
        if (activities.size() != request.getActivityIds().size()) {
            throw new IllegalArgumentException("Activity list does not match itinerary day");
        }

        Set<Long> currentIds = new HashSet<>(activities.stream().map(Activity::getId).toList());
        Set<Long> requestedIds = new HashSet<>(request.getActivityIds());

        if (!currentIds.equals(requestedIds)) {
            throw new IllegalArgumentException("Activity list does not match itinerary day");
        }

        int index = 1;
        for (Long activityId : request.getActivityIds()) {
            Activity activity = activities.stream()
                    .filter(item -> item.getId().equals(activityId))
                    .findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Activity not found"));
            activity.setSortOrder(index++);
        }

        activityRepository.saveAll(activities);

        return activityRepository.findAllByItineraryDayIdOrderBySortOrderAscCreatedAtAsc(request.getItineraryDayId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ActivityResponse update(Long activityId, ActivityUpdateRequest request, User user) {

        Activity activity = activityRepository.findOwnedById(activityId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        validateCost(request.getCost());

        // Aqui eu atualizo so os campos que vieram na requisicao (estilo PATCH).
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
                .sortOrder(a.getSortOrder())
                .createdAt(a.getCreatedAt())
                .build();
    }

    private Integer nextSortOrder(Long itineraryDayId) {
        Integer maxSortOrder = activityRepository.findMaxSortOrderByItineraryDayId(itineraryDayId);
        return (maxSortOrder == null ? 0 : maxSortOrder) + 1;
    }

    private void normalizeSortOrder(Long itineraryDayId) {
        List<Activity> activities = activityRepository.findAllByItineraryDayIdOrderBySortOrderAscCreatedAtAsc(itineraryDayId);
        boolean changed = false;

        for (int index = 0; index < activities.size(); index++) {
            Activity activity = activities.get(index);
            if (activity.getSortOrder() == null || !activity.getSortOrder().equals(index + 1)) {
                activity.setSortOrder(index + 1);
                changed = true;
            }
        }

        if (changed) {
            activityRepository.saveAll(activities);
        }
    }
}
