package com.travelapp.activity.domain;

import com.travelapp.itinerary.domain.ItineraryDay;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "activities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_day_id", nullable = false)
    private ItineraryDay itineraryDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityType type;

    @Column(nullable = false, length = 160)
    private String title;

    @Column(length = 200)
    private String place;

    @Column(length = 2000)
    private String notes;

    // Deixo o horario opcional porque eu posso planejar sem hora definida.
    private LocalTime time;

    // Deixo o custo opcional para eu registrar so quando fizer sentido.
    @Column(precision = 12, scale = 2)
    private BigDecimal cost;

    @Column
    private Integer sortOrder;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
