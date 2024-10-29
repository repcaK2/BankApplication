package com.example.bankapplication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final UserService userService;
	private final AdminService adminService;

	@GetMapping("/user/all")
	public ResponseEntity<List<User>> getAll() {
		List<User> users = userService.getAll();
		return ResponseEntity.ok().body(users);
	}

	@PostMapping("/user/id")
	public ResponseEntity<User> findUserById(
			@RequestParam Long id
	) {
		User foundUserById = userService.findUserById(id);
		return ResponseEntity.ok().body(foundUserById);
	}

	@PostMapping("/user/email")
	public ResponseEntity<User> findUserById(
			@RequestParam String email
	) {
		User foundUserByEmail = userService.findUserByEmail(email);
		return ResponseEntity.ok().body(foundUserByEmail);
	}

	@PostMapping("/user/ban")
	public ResponseEntity<String> banUser(
			@RequestParam String email
	) {
		return ResponseEntity.ok(adminService.banUser(email));
	}
}
