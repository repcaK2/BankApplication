package com.example.bankapplication.loan.MonthlyPaymentCalculator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class LoanCalculationService {

	private final KafkaTemplate<String, String> kafkaTemplate;

	@Value("${spring.loan.interest_rate}")
	private BigDecimal interestRate;

	public LoanCalculationResult calculateMonthlyPayment(
			BigDecimal loanAmount,
			int loanTermMonths
	) {
		if (loanAmount.compareTo(BigDecimal.ZERO) <= 0) {
			kafkaTemplate.send("loanException", "Exception thrown: " + "Loan amount should be greater than 0");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan amount should be greater than 0");
		}

		if (loanTermMonths <= 0) {
			kafkaTemplate.send("loanException", "Exception thrown: " + "Loan term month should be greater than 0");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loan term month should be greater than 0");
		}

		BigDecimal adjustedAnnualInterestRate = interestRate.divide(BigDecimal.valueOf(100), 10, BigDecimal.ROUND_HALF_UP);

		BigDecimal monthlyInterestRate = adjustedAnnualInterestRate.divide(BigDecimal.valueOf(12), 10, BigDecimal.ROUND_HALF_UP);

		// Calculating (1 + r)^(-n)
		BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyInterestRate);
		BigDecimal onePlusRateToPower = BigDecimal.ONE.divide(onePlusRate.pow(loanTermMonths), 10, BigDecimal.ROUND_HALF_UP);

		// Monthly: (loanAmount * monthlyInterestRate) / (1 - (1 + r)^(-n))
		BigDecimal numerator = loanAmount.multiply(monthlyInterestRate);

		BigDecimal denominator = BigDecimal.ONE.subtract(onePlusRateToPower);

		if (denominator.compareTo(BigDecimal.ZERO) == 0) {
			kafkaTemplate.send("loanException", "Exception thrown: " + "Denominator should be greater than 0");
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Denominator should be greater than 0");
		}

		BigDecimal monthlyPayment = numerator.divide(denominator, 10, BigDecimal.ROUND_HALF_UP);

		monthlyPayment = monthlyPayment.setScale(2, BigDecimal.ROUND_HALF_UP);

		BigDecimal totalPayment = monthlyPayment.multiply(BigDecimal.valueOf(loanTermMonths));
		totalPayment = totalPayment.setScale(2, BigDecimal.ROUND_HALF_UP);

		return new LoanCalculationResult(monthlyPayment, totalPayment);
	}
}
