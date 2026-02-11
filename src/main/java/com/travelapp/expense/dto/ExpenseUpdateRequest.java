package com.travelapp.expense.dto;

import com.travelapp.expense.domain.ExpenseCategory;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseUpdateRequest {

    @Positive
    private BigDecimal amount;

    private ExpenseCategory category;

    private String title;

    private String currency;

    private LocalDateTime spentAt;
}
