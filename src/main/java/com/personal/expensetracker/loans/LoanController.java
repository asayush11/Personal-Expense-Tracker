package com.personal.expensetracker.loans;
import com.personal.expensetracker.utilities.APIResponse;
import com.personal.expensetracker.utilities.JWTUtil;
import com.personal.expensetracker.users.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanService loanService;
    private final JWTUtil jwtUtil;

    public LoanController(LoanService loanService, JWTUtil jwtUtil) {
        this.loanService = loanService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/view")
    public ResponseEntity<APIResponse<List<LoanDTO>>> displayLoans(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            List<Loan> loans = loanService.getAllLoans(email);
            Map<String, DoubleSummaryStatistics> loanSummary = loans.stream()
                    .collect(Collectors.groupingBy(
                            Loan::getName,
                            Collectors.summarizingDouble(Loan::getAmount)
                    ));
            List<LoanDTO> loanDTOs = loanSummary.entrySet().stream()
                    .map(entry -> new LoanDTO(
                            entry.getKey(),
                            entry.getValue().getSum()
                    )).toList();
            return ResponseEntity.ok(APIResponse.success("Loans fetched successfully", loanDTOs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        }
    }

    @GetMapping("/view/lent")
    public ResponseEntity<APIResponse<List<LoanDTO>>> displayLoansGiven(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            List<Loan> loans = loanService.findAllLoansGiven(email);
            List<LoanDTO> loanDTOs = loans.stream().map(Loan::convertToDTO).toList();
            return ResponseEntity.ok(APIResponse.success("Loans fetched successfully", loanDTOs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        }
    }

    @GetMapping("/view/taken")
    public ResponseEntity<APIResponse<List<LoanDTO>>> displayLoansTaken(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            List<Loan> loans = loanService.findAllLoansTaken(email);
            List<LoanDTO> loanDTOs = loans.stream().map(Loan::convertToDTO).toList();
            return ResponseEntity.ok(APIResponse.success("Loans fetched successfully", loanDTOs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        }
    }

    @GetMapping("/view{id}")
    public ResponseEntity<APIResponse<LoanDTO>> getLoanById(@RequestHeader("Authorization") String authHeader, @PathVariable Long id){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            Loan loan = loanService.getLoanById(id, email)
                    .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + id));
            return ResponseEntity.ok(APIResponse.success("Loan fetched successfully", loan.convertToDTO()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch loan", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to fetch loan", e.getMessage()));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<APIResponse<LoanDTO>> addLoan(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody Loan loan){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            User user = new User();
            user.setEmail(email);
            loan.setUser(user);
            Loan savedLoan = loanService.addLoan(loan);
            return ResponseEntity.ok(APIResponse.success("Loan added successfully", savedLoan.convertToDTO()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to add loan", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to add loan", e.getMessage()));
        }
    }

    @PutMapping("/edit")
    public ResponseEntity<APIResponse<LoanDTO>> editLoanById(@RequestHeader("Authorization") String authHeader, @Valid @RequestBody Loan updatedLoan){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        Long id = updatedLoan.getId();
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            Loan existingLoan = loanService.getLoanById(id, email)
                    .orElseThrow(() -> new RuntimeException("Loan not found with ID: " + id));
            Loan loan = loanService.editLoan(existingLoan,updatedLoan);
            return ResponseEntity.ok(APIResponse.success("Loan edited successfully", loan.convertToDTO()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to edit loan", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to edit loan", e.getMessage()));
        }
    }

    @DeleteMapping("/remove{id}")
    @Transactional
    public ResponseEntity<APIResponse<Void>> removeLoan(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        if (authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        String email;
        try {
            String token = authHeader.replace("Bearer", "");
            email = jwtUtil.validateToken(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to delete loan", e.getMessage()));
        }
        if (!loanService.deleteLoan(id, email)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(APIResponse.error("Failed to delete loan", "Loan not found with ID: " + id));
        }
        return ResponseEntity.ok(APIResponse.success("Loan Deleted Successfully", null));
    }

    @DeleteMapping("/remove")
    @Transactional
    public ResponseEntity<APIResponse<Void>> settleFriend(@RequestHeader("Authorization") String authHeader, @RequestParam String name) {
        if (authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        String email;
        try {
            String token = authHeader.replace("Bearer", "");
            email = jwtUtil.validateToken(token);
            loanService.settleFriend(name, email);
            return ResponseEntity.ok(APIResponse.success("Loan Settled Successfully with: " + name, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to settle loan", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to settle loan", e.getMessage()));
        }

    }

    @GetMapping("/net")
    public ResponseEntity<APIResponse<Double>> getNetLoans(@RequestHeader("Authorization") String authHeader){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            Double totalLoans = loanService.getNetLoan(email);
            if(totalLoans == null) totalLoans = 0.0;
            return ResponseEntity.ok(APIResponse.success("Net Loan fetched successfully", totalLoans));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch loans", e.getMessage()));
        }
    }

    @GetMapping("/net{modeOfPayment}")
    public ResponseEntity<APIResponse<Double>> getTotalLentByPaymentMode(@RequestHeader("Authorization") String authHeader, @PathVariable String modeOfPayment){
        if(authHeader == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Unauthorized Access", "Please login"));
        try {
            String token = authHeader.replace("Bearer","");
            String email = jwtUtil.validateToken(token);
            Double totalLent = loanService.getTotalLentByPaymentMode(email, modeOfPayment);
            if(totalLent == null) totalLent = 0.0;
            return ResponseEntity.ok(APIResponse.success("Total Lent fetched successfully for payment mode: " + modeOfPayment, totalLent));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.error("Failed to fetch expenses", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(APIResponse.error("Failed to fetch expenses", e.getMessage()));
        }
    }

}
