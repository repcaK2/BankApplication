package com.example.bankapplication.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements IUserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public List<User> getAll() {
		return userRepository.findAll();
	}

	@Override
	public User findUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	@Override
	public String updateEmail(String email, String newEmail, String password) {

		User storedUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

		if(!passwordEncoder.matches(password, storedUser.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect");
		}

		if (email.equals(newEmail)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New email must be different from the older");
		}

		storedUser.setEmail(newEmail);
		userRepository.save(storedUser);

		return "Email has been updated";
	}

	@Override
	public String updatePassword(String email, String oldPassword, String newPassword) {

		User storedUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

		String storedPassword = storedUser.getPassword();

		if (oldPassword.equals(newPassword)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password cannot be the same as the old password in the form");
		}

		if (!passwordEncoder.matches(oldPassword, storedPassword)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old PASSWORD is incorrect");
		}

		if (passwordEncoder.matches(newPassword, storedPassword)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New Password must be different form older");
		}

		if (newPassword.length() < 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password should have at least 8 characters long");
		}

		storedUser.setPassword(passwordEncoder.encode(newPassword));

		userRepository.save(storedUser);
		return "Password has been updated";
	}

	@Override
	public String updatePin(String email, String oldPin, String newPin, String password) {

		User storedUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

		String storedPin = storedUser.getPin();

		if(!passwordEncoder.matches(password, storedUser.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect");
		}

		if (oldPin.equals(newPin)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New PIN cannot be the same as the old PIN in the form");
		}

		if (passwordEncoder.matches(oldPin, storedPin)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old PIN is incorrect");
		}

		if (passwordEncoder.matches(newPin, storedPin)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New PIN must be different from the older");
		}

		if (newPin.length() < 5) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PIN should have at least 5 characters long");
		}

		storedUser.setPin(passwordEncoder.encode(newPin));

		userRepository.save(storedUser);

		return "Password has been updated";
	}

	@Override
	public String updatePhoneNumber(String email, String oldPhoneNumber, String newPhoneNumber, String password) {

		User storedUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));

		String storedPhoneNumber = storedUser.getPhoneNumber();

		if(!passwordEncoder.matches(password, storedUser.getPassword())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is incorrect");
		}

		if (oldPhoneNumber.equals(newPhoneNumber)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New Phone Number cannot be the same as the old Phone Number in the form");
		}

		if (passwordEncoder.matches(oldPhoneNumber, storedPhoneNumber)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old Phone number is incorrect");
		}

		if (passwordEncoder.matches(newPhoneNumber, storedPhoneNumber)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New Phone Number must be different from the older");
		}

		storedUser.setPhoneNumber(newPhoneNumber);

		userRepository.save(storedUser);
		return "Phone Number has been updated";
	}
}
