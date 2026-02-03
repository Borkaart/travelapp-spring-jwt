package com.travelapp.expense.api;

import com.travelapp.entity.User;
import com.travelapp.expense.dto.ExpenseCreateRequest;
import com.travelapp.expense.dto.ExpenseResponse;
import com.travelapp.expense.dto.ExpenseSummaryResponse;
import com.travelapp.expense.dto.ExpenseUpdateRequest;
import com.travelapp.expense.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(
            @RequestBody ExpenseCreateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return expenseService.create(request, user);
    }

    @GetMapping("/trip/{tripId}")
    public List<ExpenseResponse> listByTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User user
    ) {
        return expenseService.listByTrip(tripId, user);
    }

    @PutMapping("/{expenseId}")
    public ExpenseResponse update(
            @PathVariable Long expenseId,
            @RequestBody ExpenseUpdateRequest request,
            @AuthenticationPrincipal User user
    ) {
        return expenseService.update(expenseId, request, user);
    }
    @GetMapping("/trip/{tripId}/summary")
    public ExpenseSummaryResponse summary(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User user
    ) {
        return expenseService.summary(tripId, user);
    }

    @DeleteMapping("/{expenseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal User user
    ) {
        expenseService.delete(expenseId, user);
    }
}
