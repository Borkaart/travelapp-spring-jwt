package com.travelapp.budget.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BudgetStatusResponse {
    private Long tripId;
    private String currency;
    private BigDecimal limitAmount;
    private BigDecimal totalSpent;
    private BigDecimal remaining;
    private boolean exceeded;
}
