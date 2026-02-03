package com.travelapp.activity.repository;

import com.travelapp.activity.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findAllByItineraryDayIdOrderByTimeAscCreatedAtAsc(Long itineraryDayId);

    @Query("""
        select count(a)
        from Activity a
        where a.itineraryDay.trip.id = :tripId
    """)
    long countByTripId(@Param("tripId") Long tripId);
}
