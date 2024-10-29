package com.example.bankapplication.loan.MonthlyPaymentCalculator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoanCalculationResult {

	private BigDecimal monthlyPayment;
	private BigDecimal totalPayment;
}
