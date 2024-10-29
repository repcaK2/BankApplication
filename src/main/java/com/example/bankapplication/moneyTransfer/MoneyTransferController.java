package com.example.bankapplication.moneyTransfer;

import com.example.bankapplication.history.TransactionHistoryRepository;
import com.example.bankapplication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MoneyTransferController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TransactionHistoryRepository historyRepository;
	private final MoneyTransferService moneyTransferService;

	@PostMapping("/transferMoney")
	public ResponseEntity<String> transferMoney(
			Principal principal,
			@RequestBody TransferMoney transferMoney
	) {
		String senderEmail = principal.getName();
		String receiverEmail = transferMoney.getReceiverEmail();
		BigDecimal amountToSend = transferMoney.getBalanceToSend();
		String pinRequested = transferMoney.getPin();
		String description = transferMoney.getDescription();
		String transactionType = transferMoney.getTransactionType();

		return ResponseEntity.ok(moneyTransferService.transferMoney(principal, transferMoney));
	}
}

