package com.example.bankapplication.kafkaListener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BlikNotificationListener {

	@KafkaListener(groupId = "deafult", topics = "loanException")
	void blikListener(String data) {
		System.out.println(data);
	}
}
