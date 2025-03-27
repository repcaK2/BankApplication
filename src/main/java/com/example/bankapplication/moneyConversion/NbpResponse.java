package com.example.bankapplication.moneyConversion;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class NbpResponse {
	private List<NbpRate> rates;

	@Data
	public static class NbpRate {
		private BigDecimal mid;
	}
}
