package com.travelapp.budget.service;

import com.travelapp.budget.domain.Budget;
import com.travelapp.budget.dto.BudgetResponse;
import com.travelapp.budget.dto.BudgetStatusResponse;
import com.travelapp.budget.dto.BudgetUpsertRequest;
import com.travelapp.budget.repository.BudgetRepository;
import com.travelapp.entity.User;
import com.travelapp.expense.repository.ExpenseRepository;
import com.travelapp.trip.domain.Trip;
import com.travelapp.trip.repository.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional
    public BudgetResponse upsert(BudgetUpsertRequest request, User user) {
        Trip trip = tripRepository.findById(request.getTripId())
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        assertOwner(trip, user);

        Budget budget = budgetRepository.findByTripId(trip.getId())
                .orElseGet(() -> Budget.builder()
                        .trip(trip)
                        .createdAt(LocalDateTime.now())
                        .build());

        budget.setLimitAmount(request.getLimitAmount());
        budget.setCurrency(request.getCurrency());
        budget.setUpdatedAt(LocalDateTime.now());

        Budget saved = budgetRepository.save(budget);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BudgetResponse getByTrip(Long tripId, User user) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        assertOwner(trip, user);

        Budget budget = budgetRepository.findByTripId(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));

        return toResponse(budget);
    }

    @Transactional(readOnly = true)
    public BudgetStatusResponse status(Long tripId, User user) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found"));

        assertOwner(trip, user);

        Budget budget = budgetRepository.findByTripId(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Budget not found"));

        BigDecimal totalSpent = expenseRepository.sumAmountByTrip(tripId);
        BigDecimal remaining = budget.getLimitAmount().subtract(totalSpent);
        boolean exceeded = remaining.signum() < 0;

        return BudgetStatusResponse.builder()
                .tripId(tripId)
                .currency(budget.getCurrency())
                .limitAmount(budget.getLimitAmount())
                .totalSpent(totalSpent)
                .remaining(remaining)
                .exceeded(exceeded)
                .build();
    }

    private void assertOwner(Trip trip, User user) {
        if (!trip.getOwner().getId().equals(user.getId())) {
            throw new AccessDeniedException("Not your trip");
        }
    }

    private BudgetResponse toResponse(Budget b) {
        return BudgetResponse.builder()
                .id(b.getId())
                .tripId(b.getTrip().getId())
                .limitAmount(b.getLimitAmount())
                .currency(b.getCurrency())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}
