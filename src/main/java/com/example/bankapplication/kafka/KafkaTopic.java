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

	@Bean
	public NewTopic test() {
		return TopicBuilder
				.name(topic_test)
				.build();
	}
}
