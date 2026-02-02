package com.travelapp.trip.repository;

import com.travelapp.trip.domain.Trip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Page<Trip> findAllByOwnerId(Long ownerId, Pageable pageable);

    boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
