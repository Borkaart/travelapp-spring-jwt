package com.travelapp.social.entity;

import com.travelapp.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 500)
    private String bio;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String city;

    private String country;

    @Column(name = "instagram_link")
    private String instagramLink;

    @Column(name = "facebook_link")
    private String facebookLink;

    @Column(name = "website_link")
    private String websiteLink;

    private String gender;

    @Column(name = "theme_preference")
    @Builder.Default
    private String themePreference = "SYSTEM";

    @Column(name = "notifications_enabled")
    @Builder.Default
    private boolean notificationsEnabled = true;

    // Métodos utilitários podem ser adicionados aqui
}
