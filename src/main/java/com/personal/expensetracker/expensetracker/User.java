package com.personal.expensetracker.expensetracker;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchConnectionDetails;

import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    @Id
    @Column(nullable = false, unique = true)
    @NotNull(message = "Enter email")
    private String email;

    @NotNull(message = "Enter name")
    private String name;

    @NotNull(message = "Enter password")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses;

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
}
