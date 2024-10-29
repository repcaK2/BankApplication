package com.example.bankapplication.moneyTransfer;

import com.example.bankapplication.history.TransactionHistory;
import com.example.bankapplication.history.TransactionHistoryRepository;
import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class MoneyTransferService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TransactionHistoryRepository historyRepository;

	public String transferMoney(
			Principal principal,
			TransferMoney transferMoney
	) {
		String senderEmail = principal.getName();
		String receiverEmail = transferMoney.getReceiverEmail();
		BigDecimal amountToSend = transferMoney.getBalanceToSend();
		String pinRequested = transferMoney.getPin();
		String description = transferMoney.getDescription();
		String transactionType = transferMoney.getTransactionType();

		User sender = userRepository.findByEmail(senderEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + senderEmail));

		if (amountToSend.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ammount to send: " + amountToSend);
		}

		// Finding pin
		String storedPin = userRepository.findPinByEmail(senderEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pin not found with email: " + senderEmail));

		// Finding Receiver
		User receiver = userRepository.findByEmail(receiverEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Receiver not found with email: " + receiverEmail));

		// If user have enought money
		if (sender.getAccountBalance().compareTo(amountToSend) < 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds.");
		}

		// Check if pins match
		if (!passwordEncoder.matches(pinRequested, storedPin)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect PIN.");
		}

		// Balance update
		BigDecimal newSenderBalance = sender.getAccountBalance().subtract(amountToSend);
		BigDecimal newReceiverBalance = receiver.getAccountBalance().add(amountToSend);

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
		return "Funds has been sent.";
	}
}
