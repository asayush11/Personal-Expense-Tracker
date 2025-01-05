package com.personal.expensetracker.expensetracker;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/view")
    public ResponseEntity<APIResponse<List<Expense>>> displayExpenses(){
        try {
            List<Expense> expenses = expenseService.getAllExpenses();
            return ResponseEntity.ok(APIResponse.success("Expenses fetched successfully", expenses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch expenses", e.getMessage()));
        }
    }

    @GetMapping("/view{id}")
    public ResponseEntity<APIResponse<Expense>> getExpenseById(@PathVariable Long id){
        try {
            Expense expense = expenseService.getExpenseById(id)
                              .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
            return ResponseEntity.ok(APIResponse.success("Expense fetched successfully", expense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to fetch expense", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<APIResponse<Expense>> addExpense(@Valid @RequestBody Expense expense){
        try {
            Expense savedExpense = expenseService.addExpense(expense);
            return ResponseEntity.ok(APIResponse.success("Expense added successfully", savedExpense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to add expense", e.getMessage()));
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<APIResponse<Expense>> editExpenseById(@Valid @RequestBody Expense updatedExpense){
        Long id = updatedExpense.getId();
        try {
            Expense existingExpense = expenseService.getExpenseById(id)
                    .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
            Expense expense = expenseService.editExpense(existingExpense,updatedExpense);
            return ResponseEntity.ok(APIResponse.success("Expense edited successfully", expense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to fetch expense", e.getMessage()));
        }
    }

    @DeleteMapping("/remove{id}")
    public ResponseEntity<APIResponse<Void>> removeExpense(@PathVariable Long id){
        if(!expenseService.deleteExpense(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to delete expense", "Expense not found with ID: " + id));
        }
        return ResponseEntity.ok(APIResponse.success("Expense Deleted Successfully", null));
    }

    @GetMapping("/total")
    public ResponseEntity<APIResponse<Double>> getTotalExpenses(){
        try {
            Double totalExpenses = expenseService.getTotalExpenses();
            if(totalExpenses == null) totalExpenses = 0.0;
            return ResponseEntity.ok(APIResponse.success("Expense fetched successfully", totalExpenses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch expense", e.getMessage()));
        }
    }

}