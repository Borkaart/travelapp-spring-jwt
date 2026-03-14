package com.travelapp.social.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateProfileDto {
    private String name;
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
    private Boolean notificationsEnabled;
}
