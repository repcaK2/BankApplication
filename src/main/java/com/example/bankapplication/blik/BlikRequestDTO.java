package com.example.bankapplication.blik;

import lombok.Data;

@Data
public class BlikRequestDTO {

	private String blikCode;
	private String requesterEmail;
	private double requestedFunds;
	private String status;
	private String message;

	public BlikRequestDTO(String blikCode, String requesterEmail, double requestedFunds, String status, String message) {
		this.blikCode = blikCode;
		this.requesterEmail = requesterEmail;
		this.requestedFunds = requestedFunds;
		this.status = status;
		this.message = message;
	}
}
