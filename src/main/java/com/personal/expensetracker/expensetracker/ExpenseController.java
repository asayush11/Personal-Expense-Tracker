package com.personal.expensetracker.expensetracker;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final JWTUtil jwtUtil;

    public ExpenseController(ExpenseService expenseService, JWTUtil jwtUtil) {
        this.expenseService = expenseService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/view")
    public ResponseEntity<APIResponse<List<ExpenseDTO>>> displayExpenses(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            List<Expense> expenses = expenseService.getAllExpenses(email);
            List<ExpenseDTO> expenseDTOs = expenses.stream().map(Expense::convertToDTO).toList();
            return ResponseEntity.ok(APIResponse.success("Expenses fetched successfully", expenseDTOs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch expenses", e.getMessage()));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch expenses", e.getMessage()));
        }
    }

    @GetMapping("/view{id}")
    public ResponseEntity<APIResponse<ExpenseDTO>> getExpenseById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            Expense expense = expenseService.getExpenseById(id, email)
                              .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
            return ResponseEntity.ok(APIResponse.success("Expense fetched successfully", expense.convertToDTO()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch expense", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to fetch expense", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<APIResponse<ExpenseDTO>> addExpense(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody Expense expense){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            User user = new User();
            user.setEmail(email);
            expense.setUser(user);
            Expense savedExpense = expenseService.addExpense(expense);
            return ResponseEntity.ok(APIResponse.success("Expense added successfully", savedExpense.convertToDTO()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to add expense", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to add expense", e.getMessage()));
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<APIResponse<ExpenseDTO>> editExpenseById(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody Expense updatedExpense){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        Long id = updatedExpense.getId();
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            Expense existingExpense = expenseService.getExpenseById(id, email)
                    .orElseThrow(() -> new RuntimeException("Expense not found with ID: " + id));
            Expense expense = expenseService.editExpense(existingExpense,updatedExpense);
            return ResponseEntity.ok(APIResponse.success("Expense edited successfully", expense.convertToDTO()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to edit expense", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to edit expense", e.getMessage()));
        }
    }

    @DeleteMapping("/remove{id}")
    public ResponseEntity<APIResponse<Void>> removeExpense(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        String email;
        try {
            String token = authHeader.replace("Bearer", "");
            email = jwtUtil.validateToken(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to delete expense", e.getMessage()));
        }
        if (!expenseService.deleteExpense(id, email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to delete expense", "Expense not found with ID: " + id));
        }
        return ResponseEntity.ok(APIResponse.success("Expense Deleted Successfully", null));
    }

    @GetMapping("/total")
    public ResponseEntity<APIResponse<Double>> getTotalExpenses(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            Double totalExpenses = expenseService.getTotalExpenses(email);
            if(totalExpenses == null) totalExpenses = 0.0;
            return ResponseEntity.ok(APIResponse.success("Expense fetched successfully", totalExpenses));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch expenses", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch expenses", e.getMessage()));
        }
    }

}
