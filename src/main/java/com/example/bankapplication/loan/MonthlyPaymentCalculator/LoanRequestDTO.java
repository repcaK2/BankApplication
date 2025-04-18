package com.example.bankapplication.loan.MonthlyPaymentCalculator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequestDTO {

	private BigDecimal loanAmount;
	private int loanTermMonths;
}
