package com.example.bankapplication.blik;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BlikRequestDTO {

	private String blikCode;
	private String requesterEmail;
	private BigDecimal requestedFunds;
	private String status;
	private String message;

	public BlikRequestDTO(String blikCode, String requesterEmail, BigDecimal requestedFunds, String status, String message) {
		this.blikCode = blikCode;
		this.requesterEmail = requesterEmail;
		this.requestedFunds = requestedFunds;
		this.status = status;
		this.message = message;
	}
}
