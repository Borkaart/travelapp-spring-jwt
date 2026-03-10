package com.travelapp.expense.domain;

import com.travelapp.trip.domain.Trip;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Lembrete meu: o dono vem de forma indireta (expense -> trip -> owner).
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExpenseCategory category;

    @Column(length = 120)
    private String title; // Exemplo meu: "Uber aeroporto".

    @Column(length = 3)
    private String currency; // Eu uso codigos como "BRL" e "USD" quando precisar.

    private LocalDateTime spentAt; // Eu preencho quando quiser registrar o momento do gasto.

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
