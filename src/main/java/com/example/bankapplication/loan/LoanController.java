package com.example.bankapplication.loan;

import com.example.bankapplication.loan.MonthlyPaymentCalculator.LoanRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoanController{

	private final LoanService loanService;

	@PostMapping("/loan/create")
	public ResponseEntity<LoanDTO> createLoan(
			@RequestBody LoanRequestDTO loanRequestDTO,
			Principal principal
	) {
		LoanDTO newLoan = loanService.createLoan(
				loanRequestDTO.getLoanAmount(),
				loanRequestDTO.getLoanTermMonths(),
				principal.getName()
		);
		return ResponseEntity.ok().body(newLoan);
	}

	@GetMapping("/admin/loan/all")
	public ResponseEntity<List<Loan>> getAll() {
		return ResponseEntity.ok().body(loanService.getAllLoans());
	}
}
