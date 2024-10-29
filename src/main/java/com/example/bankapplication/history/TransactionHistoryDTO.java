package com.example.bankapplication.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {

	private Date dateOfCreation;
	private BigDecimal amount;
	private String transactionType;
	private String description;
	private String receiverEmail;
	private String receiverName;

	private String senderEmail;
	private String senderName;

	public TransactionHistoryDTO(TransactionHistory transaction) {
		this.dateOfCreation = transaction.getDateOfCreation();
		this.amount = transaction.getAmount();
		this.transactionType = transaction.getTransactionType();
		this.description = transaction.getDescription();
		this.receiverEmail = transaction.getReceiverEmail();
		this.receiverName = transaction.getReceiverName();

		this.senderEmail = transaction.getUser().getEmail();
		this.senderName = transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName();
	}

	@Override
	public String toString() {
		return "TransactionHistoryDTO{" +
				"dateOfCreation=" + dateOfCreation +
				", amount=" + amount +
				", transactionType='" + transactionType + '\'' +
				", description='" + description + '\'' +
				", receiverEmail='" + receiverEmail + '\'' +
				", receiverName='" + receiverName + '\'' +
				", senderEmail='" + senderEmail + '\'' +
				", senderName='" + senderName + '\'' +
				'}';
	}
}

