package com.personal.expensetracker.loans;

import java.time.LocalDate;

public class LoanDTO {
    private Long id;
    private String name;
    private String description;
    private Double amount;
    private LocalDate date;
    private String modeOfPayment;

    public LoanDTO() { }

    public LoanDTO(String key, double sum) {
        this.name = key;
        this.amount = sum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getModeOfPayment() {
        return modeOfPayment;
    }

    public void setModeOfPayment(String modeOfPayment) {
        this.modeOfPayment = modeOfPayment;
    }
}
