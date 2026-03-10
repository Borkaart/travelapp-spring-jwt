package com.travelapp.destination.repository;

import com.travelapp.destination.domain.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DestinationRepository extends JpaRepository<Destination, Long> {
    Optional<Destination> findByNameIgnoreCaseAndCountryIgnoreCase(String name, String country);
}
