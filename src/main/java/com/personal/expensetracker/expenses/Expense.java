package com.personal.expensetracker.expenses;
import com.personal.expensetracker.users.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
//@Data
@AllArgsConstructor
//@NoArgsConstructor
@Table(name = "expense")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    public Expense(){
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ExpenseDTO convertToDTO() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setDate(date);
        expenseDTO.setAmount(amount);
        expenseDTO.setDescription(description);
        expenseDTO.setId(id);
        expenseDTO.setModeOfPayment(modeOfPayment);
        return expenseDTO;
    }
}
