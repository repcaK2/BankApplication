package com.example.bankapplication.moneyTransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMoneyDTO {

	private String receiverEmail;
	private double balanceToSend;
	private String pin;
	private String description;
	private String transactionType;
}
