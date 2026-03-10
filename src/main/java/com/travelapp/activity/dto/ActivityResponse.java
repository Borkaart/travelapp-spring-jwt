package com.travelapp.activity.dto;

import com.travelapp.activity.domain.ActivityType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ActivityResponse {

    private Long id;
    private Long itineraryDayId;

    private ActivityType type;
    private String title;
    private String place;
    private String notes;

    private LocalTime time;
    private BigDecimal cost;
    private Integer sortOrder;

    private LocalDateTime createdAt;
}
