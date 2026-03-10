package com.travelapp.expense.service;

import com.travelapp.entity.User;
import com.travelapp.exception.InvalidExpenseAmountException;
import com.travelapp.expense.domain.Expense;
import com.travelapp.expense.dto.*;
import com.travelapp.expense.repository.ExpenseRepository;
import com.travelapp.trip.domain.Trip;
import com.travelapp.trip.service.TripAccessService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final TripAccessService tripAccessService;

    @Transactional
    public ExpenseResponse create(ExpenseCreateRequest request, User user) {

        Trip trip = tripAccessService.getOwnedTrip(request.getTripId(), user);

        validateAmount(request.getAmount());

        Expense expense = Expense.builder()
                .trip(trip)
                .amount(request.getAmount())
                .category(request.getCategory())
                .title(request.getTitle())
                .currency(normalizeCurrency(request.getCurrency()))
                .spentAt(request.getSpentAt())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> listByTrip(Long tripId, User user) {

        tripAccessService.getOwnedTrip(tripId, user);

        return expenseRepository.findAllByTripIdOrderBySpentAtAscCreatedAtAsc(tripId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ExpenseResponse update(Long expenseId, ExpenseUpdateRequest request, User user) {

        Expense expense = expenseRepository.findOwnedById(expenseId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        if (request.getAmount() != null) {
            validateAmount(request.getAmount());
            expense.setAmount(request.getAmount());
        }
        if (request.getCategory() != null) {
            expense.setCategory(request.getCategory());
        }
        if (request.getTitle() != null) {
            expense.setTitle(request.getTitle());
        }
        if (request.getCurrency() != null) {
            expense.setCurrency(normalizeCurrency(request.getCurrency()));
        }
        if (request.getSpentAt() != null) {
            expense.setSpentAt(request.getSpentAt());
        }

        return toResponse(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public ExpenseSummaryResponse summary(Long tripId, User user) {

        tripAccessService.getOwnedTrip(tripId, user);

        var total = expenseRepository.sumAmountByTrip(tripId);

        var byCategory = expenseRepository.sumByCategory(tripId).stream()
                .map(row -> ExpenseCategoryTotalResponse.builder()
                        .category(row[0] == null ? null : row[0].toString())
                        .total((java.math.BigDecimal) row[1])
                        .build())
                .toList();

        return ExpenseSummaryResponse.builder()
                .tripId(tripId)
                .total(total)
                .totalsByCategory(byCategory)
                .build();
    }

    @Transactional
    public void delete(Long expenseId, User user) {

        Expense expense = expenseRepository.findOwnedById(expenseId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));

        expenseRepository.delete(expense);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidExpenseAmountException();
        }
    }

    private String normalizeCurrency(String currency) {
        if (currency == null || currency.isBlank()) return null;
        return currency.trim().toUpperCase();
    }

    private ExpenseResponse toResponse(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .tripId(e.getTrip().getId())
                .amount(e.getAmount())
                .category(e.getCategory())
                .title(e.getTitle())
                .currency(e.getCurrency())
                .spentAt(e.getSpentAt())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
