package com.example.bankapplication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserDetailsController {

	private final UserService userService;

	@PostMapping("/email")
	public ResponseEntity<?> findUserByEmail(
			Principal principal
	) {
		String email = principal.getName();
		User foundUser = userService.findUserByEmail(email);
		if (foundUser == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
		UserDTO newUserDTO = new UserDTO();
		newUserDTO.setFirstName(foundUser.getFirstName());
		newUserDTO.setLastName(foundUser.getLastName());
		newUserDTO.setEmail(foundUser.getEmail());
		newUserDTO.setPhoneNumber(foundUser.getPhoneNumber());
		newUserDTO.setAccountNumber(foundUser.getAccountNumber());
		newUserDTO.setAccountBalance(foundUser.getAccountBalance());

		return ResponseEntity.ok().body(newUserDTO);
	}

	@PostMapping("/update/email")
	public ResponseEntity<String> updateEmail(
			@RequestParam String newEmail,
			@RequestParam String password,
			Principal principal
	) {
		String email = principal.getName();
		return ResponseEntity.ok(userService.updateEmail(email, newEmail, password));
	}

	@PostMapping("/update/password")
	public ResponseEntity<String> updatePassword(
			@RequestParam String oldPassword,
			@RequestParam String newPassword,
			Principal principal
	) {
		String email = principal.getName();
		return ResponseEntity.ok(userService.updatePassword(email, oldPassword, newPassword));
	}

	@PostMapping("/update/pin")
	public ResponseEntity<String> updatePin(
			@RequestParam String oldPin,
			@RequestParam String newPin,
			Principal principal
	) {
		String email = principal.getName();
		return ResponseEntity.ok(userService.updatePassword(email, oldPin, newPin));
	}
}
