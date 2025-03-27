package com.example.bankapplication.loan;

import java.math.BigDecimal;
import java.util.List;

public interface ILoanService {

	List<Loan> getAllLoans();

	LoanDTO createLoan(
			BigDecimal loanAmount,
			int loanTermMonths,
			String userEmail
	);

	void processMonthlyLoanRepaymentsForSingleUser(
			String userEmail
	);

	void repayLoan();
}
