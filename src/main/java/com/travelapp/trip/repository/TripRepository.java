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

    @Query(value = """
    select
      t.id as tripId,
      t.title as title,
      t.start_date as startDate,
      t.end_date as endDate,
      (t.end_date - t.start_date + 1) as totalDays,
      (select count(*) from itinerary_days d where d.trip_id = t.id) as itineraryDaysCount,
      (select count(*) from activities a join itinerary_days d on d.id = a.itinerary_day_id where d.trip_id = t.id) as activitiesCount,
      (select coalesce(sum(a.cost),0) from activities a join itinerary_days d on d.id = a.itinerary_day_id where d.trip_id = t.id) as itineraryPlannedTotal,
      (select count(*) from expenses e where e.trip_id = t.id) as expensesCount,
      (select coalesce(sum(e.amount),0) from expenses e where e.trip_id = t.id) as expensesTotal,
      coalesce((select b.limit_amount from budgets b where b.trip_id = t.id),0) as budgetTotal
    from trips t
    where t.id = :tripId
""", nativeQuery = true)
    Optional<TripSummaryProjection> findTripSummary(@Param("tripId") Long tripId);
}
