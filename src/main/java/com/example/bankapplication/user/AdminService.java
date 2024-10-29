package com.example.bankapplication.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService implements IAdminService{

	private final UserRepository userRepository;

	@Override
	public String banUser(String email) {

		User storedUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));

		storedUser.setEnabled(false);
		userRepository.save(storedUser);
		return "User has been banned";
	}
}
