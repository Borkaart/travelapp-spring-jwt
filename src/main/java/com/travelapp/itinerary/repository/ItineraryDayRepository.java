package com.travelapp.itinerary.repository;

import com.travelapp.itinerary.domain.ItineraryDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ItineraryDayRepository extends JpaRepository<ItineraryDay, Long> {

    List<ItineraryDay> findAllByTripIdOrderByDateAsc(Long tripId);

    Optional<ItineraryDay> findByTripIdAndDate(Long tripId, LocalDate date);

    boolean existsByTripIdAndDate(Long tripId, LocalDate date);

    @Query("""
        select d
        from ItineraryDay d
        join fetch d.trip t
        join fetch t.owner o
        where d.id = :dayId
          and o.id = :ownerId
    """)
    Optional<ItineraryDay> findOwnedById(@Param("dayId") Long dayId, @Param("ownerId") Long ownerId);
}
