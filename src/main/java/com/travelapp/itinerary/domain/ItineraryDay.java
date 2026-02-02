package com.travelapp.itinerary.domain;

import com.travelapp.trip.domain.Trip;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "itinerary_days",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_trip_day",
                        columnNames = {"trip_id", "day_date"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItineraryDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "day_date", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
