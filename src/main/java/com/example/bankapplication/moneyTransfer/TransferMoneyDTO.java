package com.example.bankapplication.moneyTransfer;

import lombok.Data;

@Data
public class TransferMoneyDTO {

	private String receiverEmail;
	private double balanceToSend;
	private String pin;
	private String description;
	private String transactionType;
}
