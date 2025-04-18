package com.example.bankapplication.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

	private String firstName;
	private String lastName;
	private String email;
	private String phoneNumber;
	private String accountNumber;
	private BigDecimal accountBalance;
}
