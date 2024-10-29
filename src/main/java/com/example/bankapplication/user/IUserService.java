package com.example.bankapplication.user;

import java.util.List;

public interface IUserService {

	List<User> getAll();
	User findUserById(Long id);
	User findUserByEmail(String email);
	String updateEmail(String email, String newEmail, String password);
	String updatePassword(String email, String oldPassword, String newPassword);
	String updatePin(String email, String oldPin, String newPin, String password);
	String updatePhoneNumber(String email, String oldPhoneNumber, String newPhoneNumber, String password);
}
