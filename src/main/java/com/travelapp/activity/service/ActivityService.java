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

        ItineraryDay day = itineraryDayRepository.findById(request.getItineraryDayId())
                .orElseThrow(() -> new EntityNotFoundException("Itinerary day not found"));

        // ownership: day -> trip -> owner
        if (!day.getTrip().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not your trip");
        }

        // regra de domínio: custo não pode ser negativo
        if (request.getCost() != null && request.getCost().signum() < 0) {
            throw new InvalidActivityCostException();
        }

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

        ItineraryDay day = itineraryDayRepository.findById(itineraryDayId)
                .orElseThrow(() -> new EntityNotFoundException("Itinerary day not found"));

        if (!day.getTrip().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not your trip");
        }

        return activityRepository
                .findAllByItineraryDayIdOrderByTimeAscCreatedAtAsc(itineraryDayId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ActivityResponse update(Long activityId, ActivityUpdateRequest request, User user) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        // ownership: activity -> itineraryDay -> trip -> owner
        if (!activity.getItineraryDay().getTrip().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not your trip");
        }

        // regra de domínio: custo não pode ser negativo
        if (request.getCost() != null && request.getCost().signum() < 0) {
            throw new InvalidActivityCostException();
        }

        activity.setType(request.getType());
        activity.setTitle(request.getTitle());
        activity.setPlace(request.getPlace());
        activity.setNotes(request.getNotes());
        activity.setTime(request.getTime());
        activity.setCost(request.getCost());

        return toResponse(activityRepository.save(activity));
    }
    @Transactional
    public void delete(Long activityId, User user) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new EntityNotFoundException("Activity not found"));

        if (!activity.getItineraryDay().getTrip().getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not your trip");
        }

        activityRepository.delete(activity);
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
