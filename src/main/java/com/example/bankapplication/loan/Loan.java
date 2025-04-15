package com.example.bankapplication.loan;

import com.example.bankapplication.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "loan")
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private BigDecimal startLoanAmount;
	private BigDecimal leftLoanAmount;
	private int startLoanTermMonths;
	private int leftLoanTermMonths;
	private BigDecimal interestRate;
	private BigDecimal monthlyPayment;
	private BigDecimal payed;
	private Date creationTime;
	private int monthsOfDelay;
	@Enumerated(EnumType.STRING)
	private LoanStatus status;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

}
