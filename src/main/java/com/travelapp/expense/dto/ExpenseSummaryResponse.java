package com.travelapp.expense.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class ExpenseSummaryResponse {
    private Long tripId;
    private BigDecimal total;
    private List<ExpenseCategoryTotalResponse> totalsByCategory;
}
