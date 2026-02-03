package com.travelapp.expense.repository;

import com.travelapp.expense.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findAllByTripIdOrderBySpentAtAscCreatedAtAsc(Long tripId);

    @Query("""
        select coalesce(sum(e.amount), 0)
        from Expense e
        where e.trip.id = :tripId
    """)
    BigDecimal sumAmountByTrip(@Param("tripId") Long tripId);

    @Query("""
        select e.category, coalesce(sum(e.amount), 0)
        from Expense e
        where e.trip.id = :tripId
        group by e.category
        order by coalesce(sum(e.amount), 0) desc
    """)
    List<Object[]> sumByCategory(@Param("tripId") Long tripId);
    long countByTripId(Long tripId);

}