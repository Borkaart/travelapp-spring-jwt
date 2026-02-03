package com.travelapp.trip.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface TripSummaryProjection {

    Long getTripId();
    String getTitle();
    LocalDate getStartDate();
    LocalDate getEndDate();

    Integer getTotalDays();

    Long getItineraryDaysCount();
    Long getActivitiesCount();
    Long getExpensesCount();

    BigDecimal getExpensesTotal();
    BigDecimal getBudgetTotal();
}
