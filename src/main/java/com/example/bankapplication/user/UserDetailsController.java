package com.example.bankapplication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Optional;

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
		try {

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
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while searching for user: " + e.getMessage());
		}
	}
}
