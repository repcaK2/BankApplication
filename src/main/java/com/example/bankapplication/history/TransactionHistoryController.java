package com.example.bankapplication.history;

import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class TransactionHistoryController {

	private final TransactionHistoryRepository transactionHistoryRepository;
	private final UserRepository userRepository;

	@GetMapping("admin/transaction/all")
	public ResponseEntity<?> findAllTransactions() {
		try {
			List<TransactionHistory> transactionHistories = transactionHistoryRepository.findAll();

			if (transactionHistories.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No transaction found");
			}
			List<TransactionHistoryDTO> transactionDTOs = transactionHistories.stream()
					.map(TransactionHistoryDTO::new)
					.collect(Collectors.toList());

			return ResponseEntity.ok().body(transactionDTOs);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving transactions");
		}
	}

	@PostMapping("/transaction/user")
	public ResponseEntity<?> findTransactionsForUser(
			Principal principal
	) {
		String userEmail = principal.getName();

			User user = userRepository.findByEmail(userEmail)
					.orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

			List<TransactionHistory> transactionHistories = transactionHistoryRepository.findByUser(user);

			List<TransactionHistoryDTO> transactionDTOs = transactionHistories.stream()
					.map(TransactionHistoryDTO::new)
					.collect(Collectors.toList());

			return ResponseEntity.ok().body(transactionDTOs);

	}
}
