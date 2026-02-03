package com.travelapp.expense.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ExpenseCategoryTotalResponse {
    private String category;
    private BigDecimal total;
}
