package com.example.bankapplication.moneyConversion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeCurrencyRequest {

	private BigDecimal PLNamount;
	private BigDecimal EURamount;
	private int EURtoPL;
	private int PLNtoEUR;
}
