package com.example.bankapplication.loan.MonthlyPaymentCalculator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loan")
public class LoanCalculationController {

	private final LoanCalculationService loanCalculationService;

	@PostMapping("/calculateMonthlyPayment")
	public ResponseEntity<LoanCalculationResult> calculateMonthlyPayment(
			@RequestBody LoanRequestDTO loanRequestDTO
	) {
		LoanCalculationResult paymentDetails = loanCalculationService.calculateMonthlyPayment(
				loanRequestDTO.getLoanAmount(),
				loanRequestDTO.getLoanTermMonths()
		);

		return ResponseEntity.ok(paymentDetails);
	}
}
