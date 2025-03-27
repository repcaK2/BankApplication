package com.example.bankapplication.moneyConversion;

import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ConversionService {

	private final UserRepository userRepository;
	private final RestTemplate restTemplate = new RestTemplate();
	private static final String NBP_API_URL = "https://api.nbp.pl/api/exchangerates/rates/A/EUR/";


	public void exchangeCurrencyPLandEUR(
			BigDecimal PLNamount,
			BigDecimal EURamount,
			int PLtoEUR,
			int EURtoPL,
			String userEmail
	) {
		User foundUser = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

		BigDecimal accountBalance = foundUser.getAccountBalance();
		BigDecimal balanceEur = foundUser.getBalanceEur();

		if(accountBalance.compareTo(PLNamount) < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough PLN money");
		}

		if(balanceEur.compareTo(PLNamount) < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough EUR money");
		}

		ResponseEntity<NbpResponse> response = restTemplate.getForEntity(NBP_API_URL, NbpResponse.class);
		BigDecimal mid = new BigDecimal(String.valueOf(response.getBody().getRates().get(0).getMid()));

		if(EURtoPL==1) {
			BigDecimal newBalanceEur = balanceEur.subtract(EURamount);
			BigDecimal newBalancePL = accountBalance.add(EURamount.multiply(mid));
			foundUser.setBalanceEur(newBalanceEur);
			foundUser.setAccountBalance(newBalancePL);
			userRepository.save(foundUser);
		}

		if(PLtoEUR==1) {
			BigDecimal newBalancePL = accountBalance.subtract(PLNamount);
			BigDecimal newBalanceEur = balanceEur.add(PLNamount.divide(mid));
			foundUser.setBalanceEur(newBalanceEur);
			foundUser.setAccountBalance(newBalancePL);
			userRepository.save(foundUser);
		}
	}
}
