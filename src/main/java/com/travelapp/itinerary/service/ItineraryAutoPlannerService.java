package com.travelapp.itinerary.service;

import com.travelapp.itinerary.domain.ItineraryDay;
import com.travelapp.itinerary.repository.ItineraryDayRepository;
import com.travelapp.trip.domain.Trip;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItineraryAutoPlannerService {

    private final ItineraryDayRepository itineraryDayRepository;

    @Transactional
    public void ensureTripDays(Trip trip) {
        List<ItineraryDay> existingDays = itineraryDayRepository.findAllByTripIdOrderByDateAsc(trip.getId());
        Set<LocalDate> existingDates = new HashSet<>();

        for (ItineraryDay existingDay : existingDays) {
            existingDates.add(existingDay.getDate());
        }

        LocalDate currentDate = trip.getStartDate();
        while (!currentDate.isAfter(trip.getEndDate())) {
            if (!existingDates.contains(currentDate)) {
                itineraryDayRepository.save(ItineraryDay.builder()
                        .trip(trip)
                        .date(currentDate)
                        .createdAt(LocalDateTime.now())
                        .build());
            }
            currentDate = currentDate.plusDays(1);
        }
    }
}
