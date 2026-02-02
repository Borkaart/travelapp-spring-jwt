package com.travelapp.activity.dto;

import com.travelapp.activity.domain.ActivityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
public class ActivityCreateRequest {

    @NotNull
    private Long itineraryDayId;

    @NotNull
    private ActivityType type;

    @NotBlank
    private String title;

    private String place;
    private String notes;
    private LocalTime time;
    private BigDecimal cost;
}
