package com.travelapp.activity.repository;

import com.travelapp.activity.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findAllByItineraryDayIdOrderByTimeAscCreatedAtAsc(Long itineraryDayId);
}
