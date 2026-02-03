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
public class ExpenseCreateRequest {
    private Long tripId;
    private BigDecimal amount;
    private ExpenseCategory category;
    private String title;
    private String currency;      // opcional
    private LocalDateTime spentAt; // opcional
}
