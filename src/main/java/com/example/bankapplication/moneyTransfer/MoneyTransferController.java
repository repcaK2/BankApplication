package com.example.bankapplication.moneyTransfer;

import com.example.bankapplication.history.TransactionHistory;
import com.example.bankapplication.history.TransactionHistoryRepository;
import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Date;

@RestController
@RequiredArgsConstructor
@Transactional
public class MoneyTransferController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TransactionHistoryRepository historyRepository;

	@PostMapping("/transferMoney")
	public ResponseEntity<String> transferMoney(
			Principal principal,
			@RequestBody TransferMoneyDTO transferMoneyDTO
	) {
		String senderEmail = principal.getName();
		String receiverEmail = transferMoneyDTO.getReceiverEmail();
		double amountToSend = transferMoneyDTO.getBalanceToSend();
		String pinRequested = transferMoneyDTO.getPin();
		String description = transferMoneyDTO.getDescription();
		String transactionType = transferMoneyDTO.getTransactionType();


		try {
			// Finding Sender
			User sender = userRepository.findByEmail(senderEmail)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + senderEmail));

			if (amountToSend <= 0) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ammount to send: " + amountToSend);
			}

			// Finding pin
			String storedPin = userRepository.findPinByEmail(senderEmail)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pin not found with email: " + senderEmail));

			// Finding Receiver
			User receiver = userRepository.findByEmail(receiverEmail)
					.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found with email: " + receiverEmail));

			// If user have enought money
			if (sender.getAccountBalance() < amountToSend) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient funds.");
			}

			// Check if pins match
			if (!passwordEncoder.matches(pinRequested, storedPin)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect PIN.");
			}

			// Balance update
			double newSenderBalance = sender.getAccountBalance() - amountToSend;
			double newReceiverBalance = receiver.getAccountBalance() + amountToSend;

			// Setting new values
			sender.setAccountBalance(newSenderBalance);
			receiver.setAccountBalance(newReceiverBalance);

			// Update values in database
			userRepository.save(sender);
			userRepository.save(receiver);

			TransactionHistory transactionHistory = TransactionHistory.builder()
					.dateOfCreation(new Date())
					.amount(amountToSend)
					.transactionType(transactionType)
					.description(description)
					.user(sender)
					.receiverEmail(receiverEmail)
					.receiverName(receiver.getFirstName() + " " + receiver.getLastName())
					.build();

			historyRepository.save(transactionHistory);

			return ResponseEntity.ok("Transfer successful.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during the transfer.");
		}
	}
}