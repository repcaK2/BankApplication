package com.example.bankapplication.blik;

import com.example.bankapplication.history.TransactionHistory;
import com.example.bankapplication.history.TransactionHistoryRepository;
import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@RestController
@Transactional
@RequiredArgsConstructor
public class BlikController {

	private static final SecureRandom secureRandom = new SecureRandom();
	private final BlikRepository blikRepository;
	private final UserRepository userRepository;
	private final TransactionHistoryRepository transactionHistoryRepository;
	private final RequestBlikRepository requestBlikRepository;
	private final PasswordEncoder passwordEncoder;

	@Value("${spring.blik.expiration_time}")
	private Long expirationTime;

	@GetMapping("/generate/blikcode")
	public ResponseEntity<?> generateBlik(
			Principal principal
	) {
		String newBlikCode = generateBlikCode();
		String creatorEmail = principal.getName();

		Blik newBlik = Blik.builder()
				.blikCode(newBlikCode)
				.expirationTime(new Date(System.currentTimeMillis() + expirationTime))
				.creatorEmail(creatorEmail)
				.build();

		blikRepository.save(newBlik);

		BlikDTO blikDTO = new BlikDTO(
				newBlik.getBlikCode(),
				newBlik.getCreatorEmail(),
				newBlik.getExpirationTime()
		);

		return ResponseEntity.ok().body(blikDTO);
	}

	public String generateBlikCode() {
		String blikCodeString;
		do {
			int blikCode = 100000 + secureRandom.nextInt(900000);
			blikCodeString = String.valueOf(blikCode);
		} while (blikRepository.existsBlikByBlikCode(blikCodeString));

		return blikCodeString;
	}

	@PostMapping("/generate/requestBlik")
	public ResponseEntity<?> requestBlikCode(
			Principal principal,
			@RequestParam String blikCode,
			@RequestParam BigDecimal requestedFunds
	) {
		String senderEmail = principal.getName();

		if (requestedFunds.compareTo(BigDecimal.ZERO) <= 0 ) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid value of funds");
		}

		if (blikRepository.findByBlikCode(blikCode).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Blik Code not found");
		}

		if (requestBlikRepository.findByBlikCode(blikCode).isPresent()) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Transaction for this BLIK code already exists.");
		}

		RequestBlik requestBlik = RequestBlik.builder()
				.blikCode(blikCode)
				.description("BLIK")
				.requesterEmail(senderEmail)
				.requestedFunds(requestedFunds)
				.status("PENDING")
				.build();

		requestBlikRepository.save(requestBlik);

		String customMessage = "BLIK request is created and waiting for owner acceptance";

		BlikRequestDTO blikRequestDTO = new BlikRequestDTO(
				requestBlik.getBlikCode(),
				requestBlik.getRequesterEmail(),
				requestBlik.getRequestedFunds(),
				requestBlik.getStatus(),
				customMessage
		);

		return ResponseEntity.ok().body(blikRequestDTO);
	}

	@PostMapping("/generate/acceptBlik")
	@Transactional
	public ResponseEntity<?> acceptBLikRequest(
			Principal principal,
			@RequestParam String blikCode,
			@RequestParam String pin
	) {
		String ownerEmail = principal.getName();

		Blik foundBlik = blikRepository.findByBlikCode(blikCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blik not found with: " + blikCode));

		Date BlikCodeExpiration = foundBlik.getExpirationTime();
		Date currentDate = new Date();
		long expiration_Time = BlikCodeExpiration.getTime() + expirationTime;

		if (currentDate.getTime() > expiration_Time) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Blik code has expired");
		}

		RequestBlik requestBlik = requestBlikRepository.findByBlikCode(blikCode)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

		BigDecimal requestedFunds = requestBlik.getRequestedFunds();
		String requesterEmail = requestBlik.getRequesterEmail();

		User blikOwner = userRepository.findByEmail(ownerEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + ownerEmail));

		User blikRequester = userRepository.findByEmail(requesterEmail)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + requesterEmail));

		if (requestBlikRepository.findByBlikCode(blikCode).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
		}

		if (blikRepository.findByBlikCode(blikCode).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "BLIK code not found");
		}

		if (!passwordEncoder.matches(pin, blikOwner.getPin())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PIN is invalid");
		}

		if (!foundBlik.getCreatorEmail().equals(ownerEmail)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not BLIK code owner");
		}

		if (requestedFunds.compareTo(blikOwner.getAccountBalance()) > 0) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have enought funds");
		}

		BigDecimal newSenderBalance = blikOwner.getAccountBalance().subtract(requestedFunds);
		BigDecimal newRequesterBalance = blikRequester.getAccountBalance().add(requestedFunds);

		blikOwner.setAccountBalance(newSenderBalance);
		blikRequester.setAccountBalance(newRequesterBalance);

		userRepository.save(blikOwner);
		userRepository.save(blikRequester);

		TransactionHistory transactionHistory = TransactionHistory.builder()
				.dateOfCreation(new Date())
				.amount(requestedFunds)
				.transactionType("BLIK")
				.description("BLIK")
				.user(blikOwner)
				.receiverEmail(blikRequester.getEmail())
				.receiverName(blikRequester.getFirstName() + " " + blikRequester.getLastName())
				.build();

		transactionHistoryRepository.save(transactionHistory);

		requestBlikRepository.delete(requestBlik);
		blikRepository.delete(foundBlik);

		return ResponseEntity.ok().body("Funds have been sent");
	}

	@GetMapping("/admin/allBlikRequest")
	public ResponseEntity<?> findAllBlikRequest() {
		List<RequestBlik> allBlikRequest = requestBlikRepository.findAll();
		if (allBlikRequest.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Requests not found");
		}
		return ResponseEntity.ok().body(allBlikRequest);
	}

	@PostMapping("/user/blikCode")
	public ResponseEntity<?> findBlikByUser(
			Principal principal
	) {
		String userEmail = principal.getName();
		Blik foundBlik = blikRepository.findBlikByCreatorEmail(userEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Blik codes not found"));

		return ResponseEntity.ok().body(foundBlik);
	}
}
