package com.example.bankapplication.moneyTransfer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferMoney {

	private String receiverEmail;
	private BigDecimal balanceToSend;
	private String pin;
	private String description;
	private String transactionType;
}
