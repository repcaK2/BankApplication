package com.example.bankapplication.loan;

import com.example.bankapplication.loan.MonthlyPaymentCalculator.LoanCalculationResult;
import com.example.bankapplication.loan.MonthlyPaymentCalculator.LoanCalculationService;
import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanService implements ILoanService{

	private final LoanRepository loanRepository;
	private final LoanCalculationService loanCalculationService;
	private final UserRepository userRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Value("${spring.loan.interest_rate}")
	private BigDecimal interestRate;

	@Value("${spring.kafka.topic.notEnoughBalance}")
	private String topic_not_enough_balanceL;

	@Override
	public List<Loan> getAllLoans() {
		List<Loan> loans = loanRepository.findAll();
		if (loans.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "loans not found");
		}
		return loans;
	}

	@Override
	public LoanDTO createLoan(
			BigDecimal loanAmount,
			int loanTermMonths,
			String userEmail
	) {
		User foundUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + userEmail));

		LoanCalculationResult loanCalculationResult = loanCalculationService.calculateMonthlyPayment(loanAmount, loanTermMonths);

		Loan newLoan = Loan.builder()
				.startLoanAmount(loanAmount)
				.leftLoanAmount(loanAmount)
				.payed(BigDecimal.valueOf(0))
				.startLoanTermMonths(loanTermMonths)
				.leftLoanTermMonths(loanTermMonths)
				.interestRate(interestRate)
				.monthlyPayment(loanCalculationResult.getMonthlyPayment())
				.creationTime(new Date())
				.user(foundUser)
				.build();

		loanRepository.save(newLoan);

		LoanDTO loanDTO = new LoanDTO();
		loanDTO.setStartLoanAmount(newLoan.getStartLoanAmount());
		loanDTO.setLeftLoanAmount(newLoan.getLeftLoanAmount());
		loanDTO.setPayed(newLoan.getPayed());
		loanDTO.setStartLoanTermMonths(newLoan.getStartLoanTermMonths());
		loanDTO.setLeftLoanTermMonths(newLoan.getLeftLoanTermMonths());
		loanDTO.setInterestRate(newLoan.getInterestRate());
		loanDTO.setMonthlyPayment(newLoan.getMonthlyPayment());

		return loanDTO;
	}

	@Override
	public void processMonthlyLoanRepayments(
			String userEmail
	) {
		User foundUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + userEmail));

		List<Loan> userLoansList = loanRepository.findByUserId(foundUser.getId());

		if (userLoansList.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found loans for: " + userEmail);
		}

		//Setting totalMonthlyPayments to 0
		BigDecimal totalMonthlyPayments = BigDecimal.ZERO;
		for (Loan loan : userLoansList) {
			totalMonthlyPayments = totalMonthlyPayments.add(loan.getMonthlyPayment());
		}

		BigDecimal accountBalance = foundUser.getAccountBalance();
		BigDecimal balanceLeft = accountBalance.subtract(totalMonthlyPayments);

		if (balanceLeft.compareTo(BigDecimal.ZERO) < 0) {
			kafkaTemplate.send(topic_not_enough_balanceL, "user: " + userEmail + " has negative balance");
		}

		foundUser.setAccountBalance(balanceLeft);
		userRepository.save(foundUser);


	}
}
