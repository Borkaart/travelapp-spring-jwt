package com.travelapp.activity.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActivityReorderRequest {

    @NotNull
    private Long itineraryDayId;

    @NotEmpty
    private List<Long> activityIds;
}
