package com.example.bankapplication.blik;

import lombok.Data;

import java.util.Date;

@Data
public class BlikDTO {

	private String blikCode;
	private String creatorEmail;
	private Date expirationTime;

	public BlikDTO(String blikCode, String creatorEmail, Date expirationTime) {
		this.blikCode = blikCode;
		this.creatorEmail = creatorEmail;
		this.expirationTime = expirationTime;
	}
}
