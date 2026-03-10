package com.travelapp.activity.repository;

import com.travelapp.activity.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findAllByItineraryDayIdOrderBySortOrderAscCreatedAtAsc(Long itineraryDayId);

    List<Activity> findAllByItineraryDayId(Long itineraryDayId);

    @Query("""
        select coalesce(max(a.sortOrder), 0)
        from Activity a
        where a.itineraryDay.id = :itineraryDayId
    """)
    Integer findMaxSortOrderByItineraryDayId(@Param("itineraryDayId") Long itineraryDayId);

    @Query("""
        select a
        from Activity a
        join fetch a.itineraryDay d
        join fetch d.trip t
        join fetch t.owner o
        where a.id = :activityId
          and o.id = :ownerId
    """)
    Optional<Activity> findOwnedById(@Param("activityId") Long activityId, @Param("ownerId") Long ownerId);

    @Query("""
        select count(a)
        from Activity a
        where a.itineraryDay.trip.id = :tripId
    """)
    long countByTripId(@Param("tripId") Long tripId);
}
