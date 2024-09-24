package com.example.bankapplication.history;

import com.example.bankapplication.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
	List<TransactionHistory> findByUser(User user);
}
