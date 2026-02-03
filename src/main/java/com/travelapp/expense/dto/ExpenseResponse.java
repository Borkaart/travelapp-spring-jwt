package com.travelapp.expense.dto;

import com.travelapp.expense.domain.ExpenseCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {
    private Long id;
    private Long tripId;
    private BigDecimal amount;
    private ExpenseCategory category;
    private String title;
    private String currency;
    private LocalDateTime spentAt;
    private LocalDateTime createdAt;
}
