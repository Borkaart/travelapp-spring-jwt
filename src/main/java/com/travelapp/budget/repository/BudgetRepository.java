package com.travelapp.budget.repository;

import com.travelapp.budget.domain.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Optional<Budget> findByTripId(Long tripId);
    boolean existsByTripId(Long tripId);
}
