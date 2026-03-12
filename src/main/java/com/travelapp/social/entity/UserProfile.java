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

    private String phoneNumber;

    private LocalDate birthDate;

    private String city;

    private String country;

    private String instagramLink;

    private String facebookLink;

    private String websiteLink;

    // Métodos utilitários podem ser adicionados aqui
}
