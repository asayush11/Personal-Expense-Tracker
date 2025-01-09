package com.personal.expensetracker.utilities;

public class CategorisedAmounts {
    private String category;
    private double amount;

    public CategorisedAmounts(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public CategorisedAmounts() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
