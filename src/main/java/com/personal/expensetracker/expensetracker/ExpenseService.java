package com.personal.expensetracker.expensetracker;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository){
        this.expenseRepository = expenseRepository;
    }
    public List<Expense> getAllExpenses(String email){
        return expenseRepository.findAllByUser_Email(email);
    }

    public Optional<Expense> getExpenseById(Long Id, String email){
        return expenseRepository.findByIdAndUser_Email(Id, email);
    }

    public Expense addExpense(Expense expense){
        return expenseRepository.save(expense);
    }

    public Expense editExpense(Expense existingExpense, Expense updatedExpense) {
        existingExpense.setAmount(updatedExpense.getAmount());
        existingExpense.setDate(updatedExpense.getDate());
        existingExpense.setDescription(updatedExpense.getDescription());
        existingExpense.setModeOfPayment(updatedExpense.getModeOfPayment());
        return expenseRepository.save(existingExpense);
    }

    public boolean deleteExpense(Long id, String email){
        var isdeleted = true;
        if(expenseRepository.existsByIdAndUser_Email(id, email)) expenseRepository.deleteByIdAndUser_Email(id, email);
        else isdeleted = false;
        return isdeleted;
    }

    public Double getTotalExpenses(String email){
        return expenseRepository.getTotalExpenses(email);
    }

    public Double getTotalExpensesByPaymentMode(String email, String modeOfPayment){
        return expenseRepository.getTotalExpensesByPaymentMode(email, modeOfPayment);
    }
}
