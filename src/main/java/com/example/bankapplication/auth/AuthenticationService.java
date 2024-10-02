package com.example.bankapplication.auth;

import com.example.bankapplication.exceptions.AuthenticationException;
import com.example.bankapplication.security.JwtService;
import com.example.bankapplication.user.Role;
import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		var user = User.builder()
				.firstName(request.getFirstname())
				.lastName(request.getLastname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.phoneNumber(request.getPhoneNumber())
				.pin(passwordEncoder.encode(request.getPIN()))
				.accountNumber(generateAccountNumber())
				.accountBalance(1000)
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