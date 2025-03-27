package com.example.bankapplication.auth;

import com.example.bankapplication.exception.AuthenticationException;
import com.example.bankapplication.security.JwtService;
import com.example.bankapplication.user.Role;
import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	@Value("${spring.registration.created_user_account_balance}")
	private BigDecimal createdUserAccountBalance;

	@Value("${spring.registration.created_user_account_balance_eur}")
	private BigDecimal createdUserAccountBalanceEur;

	public AuthenticationResponse register(RegisterRequest request) {

		if (request.getPassword().length() < 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password should have at least 8 characters long");
		}

		if (request.getPIN().length() < 5) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PIN should have at least 5 characters long");
		}

		var user = User.builder()
				.firstName(request.getFirstname())
				.lastName(request.getLastname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.phoneNumber(request.getPhoneNumber())
				.pin(passwordEncoder.encode(request.getPIN()))
				.accountNumber(generateAccountNumber())
				.accountBalance(createdUserAccountBalance)
				.balanceEur(createdUserAccountBalanceEur)
				.role(Role.USER)
				.isEnabled(true)
				.build();
		repository.save(user);
		var jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							request.getEmail(),
							request.getPassword()
					)
			);
		} catch (Exception e) {
			throw new AuthenticationException("Authentication failed: " + e.getMessage());
		}
		var user = repository.findByEmail(request.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		var jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public String generateAccountNumber() {
		Random random = new Random();
		String accountNumber;

		do {
			StringBuilder accountNumberBuilder = new StringBuilder();
			for (int i = 0; i < 26; i++) {
				int digit = random.nextInt(10);
				accountNumberBuilder.append(digit);
			}
			accountNumber = accountNumberBuilder.toString();
		} while (repository.existsByAccountNumber(accountNumber));

		return accountNumber;
	}
}