package com.travelapp.trip.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class TripSummaryResponse {

    private Long tripId;
    private String title;

    private Long destinationId;
    private String destinationName;

    private LocalDate startDate;
    private LocalDate endDate;

    private long totalDays;

    private BigDecimal totalSpent;
    private long expensesCount;

    private BigDecimal budgetLimit;     // Eu deixo null quando a viagem nao tiver budget.
    private BigDecimal budgetRemaining; // Eu deixo null quando a viagem nao tiver budget.
    private boolean budgetExceeded;     // Eu deixo false quando a viagem nao tiver budget.

    private long activitiesCount;

    // Lembrete meu: esse total por categoria e opcional no resumo.
    private List<CategoryTotal> totalsByCategory;

    @Getter
    @Builder
    public static class CategoryTotal {
        private String category;
        private BigDecimal total;
    }
}
