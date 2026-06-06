package com.eta.expenseservice.repository;

import com.eta.expenseservice.entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(String userId);
    Optional<Expense> findByUserIdAndExternalId(String userId, String externalId);
    List<Expense> findByUserIdAndCreatedAtBetween(String userId, Timestamp createdAt, Timestamp endTime);
}
