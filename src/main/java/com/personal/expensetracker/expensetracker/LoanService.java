package com.personal.expensetracker.expensetracker;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LoanService {
    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }
    public List<Loan> getAllLoans(String email){
        return loanRepository.findAllByUser_Email(email);
    }

    public Optional<Loan> getLoanById(Long Id, String email){
        return loanRepository.findByIdAndUser_Email(Id, email);
    }

    public Loan addLoan(Loan loan){
        return loanRepository.save(loan);
    }

    public Loan editLoan(Loan existingLoan, Loan updatedLoan) {
        existingLoan.setAmount(updatedLoan.getAmount());
        existingLoan.setDate(updatedLoan.getDate());
        existingLoan.setDescription(updatedLoan.getDescription());
        existingLoan.setModeOfPayment(updatedLoan.getModeOfPayment());
        existingLoan.setName(updatedLoan.getName());
        return loanRepository.save(existingLoan);
    }

    public boolean deleteLoan(Long id, String email){
        var isdeleted = true;
        if(loanRepository.existsByIdAndUser_Email(id, email)) loanRepository.deleteByIdAndUser_Email(id, email);
        else isdeleted = false;
        return isdeleted;
    }

    public void settleFriend(String name, String email){
        loanRepository.settleFriend(email, name);
    }

    public Double getNetLoan(String email){
        return loanRepository.getNetLoan(email);
    }

    public List<Loan> findAllLoansTaken(String email){
        return loanRepository.findAllLoansTaken(email);
    }

    public List<Loan> findAllLoansGiven(String email){
        return loanRepository.findAllLoansGiven(email);
    }
}
