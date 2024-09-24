package com.example.bankapplication.history;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDTO {

	private Long id;
	private Date dateOfCreation;
	private double amount;
	private String transactionType;
	private String description;
	private String receiverEmail;
	private String receiverName;

	private String senderEmail;
	private String senderName;

	public TransactionHistoryDTO(TransactionHistory transaction) {
		this.id = transaction.getId();
		this.dateOfCreation = transaction.getDateOfCreation();
		this.amount = transaction.getAmount();
		this.transactionType = transaction.getTransactionType();
		this.description = transaction.getDescription();
		this.receiverEmail = transaction.getReceiverEmail();
		this.receiverName = transaction.getReceiverName();

		this.senderEmail = transaction.getUser().getEmail();
		this.senderName = transaction.getUser().getFirstName() + " " + transaction.getUser().getLastName();
	}
}

