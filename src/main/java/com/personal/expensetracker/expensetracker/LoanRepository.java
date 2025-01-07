package com.personal.expensetracker.expensetracker;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, UserBasedRepository<Loan, Long> {
    @Query("SELECT SUM(l.amount) FROM Loan l WHERE l.user.email = :email")
    public Double getNetLoan(@Param("email") String email);

    @Query("SELECT l FROM Loan l WHERE l.user.email = :email AND l.amount > 0")
    public List<Loan> findAllLoansGiven(@Param("email") String email);

    @Query("SELECT l FROM Loan l WHERE l.user.email = :email AND l.amount < 0")
    public List<Loan> findAllLoansTaken(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM Loan l WHERE l.user.email = :email AND l.name = :name")
    public void settleFriend(@Param("email") String email, @Param("name") String name);
}
