package com.travelapp.budget.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BudgetUpsertByTripRequest {

    @NotNull
    private BigDecimal limitAmount;

    @NotBlank
    private String currency;
}
