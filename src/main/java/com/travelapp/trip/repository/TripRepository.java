package com.travelapp.trip.repository;

import com.travelapp.trip.domain.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Page<Trip> findAllByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);

    @Query("""
        select
            t.id as tripId,
            t.title as title,
            t.startDate as startDate,
            t.endDate as endDate,
            (CAST(FUNCTION('date_part', 'day', t.endDate - t.startDate) as integer) + 1) as totalDays,
            (select count(d) from ItineraryDay d where d.trip.id = t.id) as itineraryDaysCount,
            (select count(a) from Activity a where a.itineraryDay.trip.id = t.id) as activitiesCount,
            (select count(e) from Expense e where e.trip.id = t.id) as expensesCount,
            (select coalesce(sum(e.amount), 0) from Expense e where e.trip.id = t.id) as expensesTotal,
            coalesce(
                (select b.limitAmount from Budget b where b.trip.id = t.id),
                0
            ) as budgetTotal
        from Trip t
        where t.id = :tripId
    """)
    Optional<TripSummaryProjection> findTripSummary(@Param("tripId") Long tripId);
}
