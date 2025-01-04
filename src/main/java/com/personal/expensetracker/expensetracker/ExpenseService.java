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
    public List<Expense> getAllExpenses(){
        return expenseRepository.findAll();
    }

    public Optional<Expense> getExpenseById(Long Id){
        return expenseRepository.findById(Id);
    }

    public Expense addExpense(Expense expense){
        return expenseRepository.save(expense);
    }
}
