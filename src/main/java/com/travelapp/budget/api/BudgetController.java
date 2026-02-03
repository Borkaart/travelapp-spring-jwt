package com.travelapp.budget.api;

import com.travelapp.budget.dto.BudgetResponse;
import com.travelapp.budget.dto.BudgetStatusResponse;
import com.travelapp.budget.dto.BudgetUpsertRequest;
import com.travelapp.budget.service.BudgetService;
import com.travelapp.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BudgetResponse upsert(
            @RequestBody @Valid BudgetUpsertRequest request,
            @AuthenticationPrincipal User user
    ) {
        return budgetService.upsert(request, user);
    }

    @GetMapping("/trip/{tripId}")
    public BudgetResponse getByTrip(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User user
    ) {
        return budgetService.getByTrip(tripId, user);
    }

    @GetMapping("/trip/{tripId}/status")
    public BudgetStatusResponse status(
            @PathVariable Long tripId,
            @AuthenticationPrincipal User user
    ) {
        return budgetService.status(tripId, user);
    }
}
