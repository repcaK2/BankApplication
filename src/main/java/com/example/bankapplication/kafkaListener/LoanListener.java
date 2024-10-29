package com.example.bankapplication.kafkaListener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LoanListener {

	@KafkaListener(groupId = "default", topics = "notEnoughBalance")
	void notEnoughBalance(String data) {
		System.out.println(data);
	}
}
