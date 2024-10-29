package com.example.bankapplication.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanDTO {

	private BigDecimal startLoanAmount;
	private BigDecimal leftLoanAmount;
	private int startLoanTermMonths;
	private int leftLoanTermMonths;
	private BigDecimal interestRate;
	private BigDecimal monthlyPayment;
	private BigDecimal payed;
}
