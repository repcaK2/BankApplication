package com.example.bankapplication.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DemoController {

	@GetMapping("/secured")
	public ResponseEntity<String> securedEndpointTest() {
		return ResponseEntity.ok().body("It is secured endpoint");
	}
}
