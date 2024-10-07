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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlikController {

	private static final SecureRandom secureRandom = new SecureRandom();
	private final BlikRepository blikRepository;
	private final UserRepository userRepository;
	private final TransactionHistoryRepository transactionHistoryRepository;
	private final RequestBlikRepository requestBlikRepository;
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final PasswordEncoder passwordEncoder;

	@Value("${spring.kafka.topic.newBlik}")
	private String topicNewBlik;

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
			@RequestParam double requestedFunds
	) {
		String senderEmail = principal.getName();

		if (requestedFunds<=0) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid value of funds");
		}

		if (blikRepository.findByBlikCode(blikCode).isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Blik Code not found");
		}

		if (requestBlikRepository.findByBlikCode(blikCode).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Transaction for this BLIK code already exists.");
		}

		RequestBlik requestBlik = RequestBlik.builder()
				.blikCode(blikCode)
				.description("BLIK")
				.requesterEmail(senderEmail)
				.requestedFunds(requestedFunds)
				.status("PENDING")
				.build();

		requestBlikRepository.save(requestBlik);
		kafkaTemplate.send(topicNewBlik, "new BLIK request!");

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
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Blik code has expired");
		}

		RequestBlik requestBlik = requestBlikRepository.findByBlikCode(blikCode)
				.orElseThrow(() -> new RuntimeException("Transaction not found"));

		double requestedFunds = requestBlik.getRequestedFunds();
		String requesterEmail = requestBlik.getRequesterEmail();

		User blikOwner = userRepository.findByEmail(ownerEmail)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + ownerEmail));

		User blikRequester = userRepository.findByEmail(requesterEmail)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + requesterEmail));

		if (requestBlikRepository.findByBlikCode(blikCode).isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found");
		}

		if (blikRepository.findByBlikCode(blikCode).isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("BLIK code not found");
		}

		if (!passwordEncoder.matches(pin, blikOwner.getPin())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PIN is invalid");
		}

		if (!foundBlik.getCreatorEmail().equals(ownerEmail)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not BLIK code owner");
		}

		if (requestedFunds > blikOwner.getAccountBalance()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have enought funds");
		}

		double newSenderBalance = blikOwner.getAccountBalance() - requestedFunds;
		double newRequesterBalance = blikRequester.getAccountBalance() + requestedFunds;

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

		kafkaTemplate.send(topicNewBlik, "Funds have been sent");

		return ResponseEntity.ok().body("BLIK transaction has been sent");
	}


	@GetMapping("/admin/allBlikRequest")
	public ResponseEntity<?> findAllBlikRequest() {
		List<RequestBlik> allBlikRequest = requestBlikRepository.findAll();
		if (allBlikRequest.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requests not found");
		}
		return ResponseEntity.ok().body(allBlikRequest);
	}

	@PostMapping("/user/blikCode")
	public ResponseEntity<?> findBlikByUser(
			Principal principal
	) {
		String userEmail = principal.getName();
		Blik foundBlik = blikRepository.findBlikByCreatorEmail(userEmail)
				.orElseThrow(() -> new RuntimeException("Blik codes not found"));

		return ResponseEntity.ok().body(foundBlik);
	}
}
