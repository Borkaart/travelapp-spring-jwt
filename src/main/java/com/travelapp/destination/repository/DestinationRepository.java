package com.travelapp.destination.repository;

import com.travelapp.destination.domain.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
}
