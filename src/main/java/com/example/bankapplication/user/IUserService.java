package com.example.bankapplication.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {

	List<User> getAll();
	User findUserById(Long id);
	User findUserByEmail(String email);
}
