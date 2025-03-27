package com.example.bankapplication.moneyConversion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/currency")
public class ConversionController {

	private final ConversionService conversionService;

	@PostMapping("/exchange")
	public ResponseEntity<String> exchangeCurrencyPLandEUR(
			@RequestBody ExchangeCurrencyRequest exchangeCurrencyRequest,
			Principal principal
	) {
		conversionService.exchangeCurrencyPLandEUR(
				exchangeCurrencyRequest.getPLNamount(),
				exchangeCurrencyRequest.getEURamount(),
				exchangeCurrencyRequest.getPLNtoEUR(),
				exchangeCurrencyRequest.getEURtoPL(),
				principal.getName()
		);
		return ResponseEntity.ok().body("exchange currency has been done");
	}
}
