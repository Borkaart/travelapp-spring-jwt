package com.travelapp.social.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserProfileDto {
    private Long id;
    private UserSummaryDto user;
    private String bio;
    private String phoneNumber;
    private LocalDate birthDate;
    private String city;
    private String country;
    private String instagramLink;
    private String facebookLink;
    private String websiteLink;
    private String gender;
    private String themePreference;
    private boolean notificationsEnabled;
}
