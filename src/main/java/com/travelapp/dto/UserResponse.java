package com.travelapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    @JsonProperty("isProfileCompleted")
    private boolean isProfileCompleted;
    @JsonProperty("profileImage")
    private String profileImage;
}
