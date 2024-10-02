package com.example.bankapplication.blik;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BlikNotificationListener {

	@KafkaListener(groupId = "deafult", topics = "topicNewBlik")
	void blikListener(String data) {
		System.out.println(data);
	}
}
