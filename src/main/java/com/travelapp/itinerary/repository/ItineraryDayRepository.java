package com.travelapp.itinerary.repository;

import com.travelapp.itinerary.domain.ItineraryDay;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItineraryDayRepository extends JpaRepository<ItineraryDay, Long> {

    List<ItineraryDay> findAllByTripIdOrderByDateAsc(Long tripId);

    Optional<ItineraryDay> findByTripIdAndDate(Long tripId, LocalDate date);

    boolean existsByTripIdAndDate(Long tripId, LocalDate date);
}
