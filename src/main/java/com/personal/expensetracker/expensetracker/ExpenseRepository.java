package com.personal.expensetracker.expensetracker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long>, UserBasedExpenseRepository<Expense, Long> {
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.user.email = :email")
    public Double getTotalExpenses(@Param("email") String email);
}
