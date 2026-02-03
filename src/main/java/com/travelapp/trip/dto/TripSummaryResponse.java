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

    private BigDecimal budgetLimit;     // null se não tiver budget
    private BigDecimal budgetRemaining; // null se não tiver budget
    private boolean budgetExceeded;     // false se não tiver budget

    private long activitiesCount;

    // opcional: total por categoria (se você quiser)
    private List<CategoryTotal> totalsByCategory;

    @Getter
    @Builder
    public static class CategoryTotal {
        private String category;
        private BigDecimal total;
    }
}
