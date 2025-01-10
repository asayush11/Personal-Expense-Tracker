package com.personal.expensetracker.users;
import com.personal.expensetracker.expenses.Expense;
import com.personal.expensetracker.loans.Loan;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @Column(nullable = false, unique = true)
    @NotNull(message = "Enter email")
    @Size(min = 1)
    private String email;

    @NotNull(message = "Enter name")
    @Size(min = 1)
    private String name;

    @NotNull(message = "Enter password")
    @Size(min = 1)
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans;

    public User(String email, String name, String password) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.expenses = new ArrayList<>();
    }

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }

    public List<Loan> getLoanRecords() {
        return loans;
    }

    public void setLoanRecords(List<Loan> loans) {
        this.loans = loans;
    }
}
