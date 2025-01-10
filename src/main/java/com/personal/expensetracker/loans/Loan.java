package com.personal.expensetracker.loans;
import com.personal.expensetracker.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Entity
@Table(name = "loan")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Enter Friend's name")
    @Size(min = 1)
    private String name;

    @NotNull(message = "Enter Description")
    @Size(min = 1)
    private String description;

    @NotNull(message = "Enter Amount")
    @Size(min = 1)
    private Double amount;

    private LocalDate date;

    private String modeOfPayment;

    @ManyToOne
    @JoinColumn(name = "user_email", nullable = false)
    private User user;

    public Loan() {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LoanDTO convertToDTO() {
        LoanDTO loanDTO = new LoanDTO();
        loanDTO.setDate(date);
        loanDTO.setName(name);
        loanDTO.setAmount(amount);
        loanDTO.setDescription(description);
        loanDTO.setId(id);
        loanDTO.setModeOfPayment(modeOfPayment);
        return loanDTO;
    }
}
