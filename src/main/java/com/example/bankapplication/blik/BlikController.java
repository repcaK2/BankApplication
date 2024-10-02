package com.example.bankapplication.blik;

import com.example.bankapplication.moneyTransfer.MoneyTransferController;
import com.example.bankapplication.moneyTransfer.TransferMoneyDTO;
import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
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

		return ResponseEntity.ok().body(newBlik);
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
				.description("BLIK money transfer")
				.requesterEmail(senderEmail)
				.requestedFunds(requestedFunds)
				.status("PENDING")
				.build();

		requestBlikRepository.save(requestBlik);
		kafkaTemplate.send(topicNewBlik, "new BLIK request!");

		return ResponseEntity.ok().body("BLIK transfer request created and awaiting approval from the owner.");
	}

	@PostMapping("/generate/acceptBlik")
	public ResponseEntity<?> acceptBLikRequest(
			Principal principal,
			@RequestParam String blikCode,
			@RequestParam String pin
	) {
		String ownerEmail = principal.getName();

		Blik foundBlik = blikRepository.findByBlikCode(blikCode)
				.orElseThrow(() -> new RuntimeException("Blik not found with: " + blikCode));

		RequestBlik requestBlik = requestBlikRepository.findByBlikCode(blikCode)
				.orElseThrow(() -> new RuntimeException("Transaction not found"));

		double requestedFunds = requestBlik.getRequestedFunds();
		String requesterEmail = requestBlik.getRequesterEmail();

		User blikOwner = userRepository.findByEmail(ownerEmail)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + ownerEmail));

		User blikRequester = userRepository.findByEmail(requesterEmail)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + ownerEmail));

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

		double newSenderBalance = blikOwner.getAccountBalance() - requestedFunds;
		double newRequesterBalance = blikRequester.getAccountBalance() + requestedFunds;

		blikOwner.setAccountBalance(newSenderBalance);
		blikRequester.setAccountBalance(newRequesterBalance);

		userRepository.save(blikOwner);
		userRepository.save(blikRequester);

		requestBlikRepository.delete(requestBlik);

		requestBlik.setStatus("ACCEPTED");
		requestBlikRepository.save(requestBlik);





		TransferMoneyDTO transferMoneyDTO = TransferMoneyDTO.builder()
				.balanceToSend(requestedFunds)
				.receiverEmail(requesterEmail)
				.description("Accepted Blik transaction")
				.pin(pin)
				.transactionType("BLIK transaction")
				.build();




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
}
