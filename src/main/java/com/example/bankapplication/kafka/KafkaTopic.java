package com.example.bankapplication.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

	@Value("${spring.kafka.topic.test}")
	private String topic_test;

	@Value("${spring.kafka.topic.newBlik}")
	private String topic_new_blik;

	@Value("${spring.kafka.topic.loanException}")
	private String topic_loan_exception;

	@Value("${spring.kafka.topic.notEnoughBalance}")
	private String topic_not_enough_balanceL;

	@Bean
	public NewTopic test() {
		return TopicBuilder
				.name(topic_test)
				.build();
	}

	@Bean
	public NewTopic newBlik() {
		return TopicBuilder
				.name(topic_new_blik)
				.build();
	}

	@Bean
	public NewTopic loanException() {
		return TopicBuilder
				.name(topic_loan_exception)
				.build();
	}

	@Bean
	public NewTopic notEnoughBalance() {
		return TopicBuilder
				.name(topic_not_enough_balanceL)
				.build();
	}
}
