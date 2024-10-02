package com.example.bankapplication.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByAccountNumber(String accountNumber);

	@Query("SELECT u.pin FROM User u WHERE u.email = :email")
	Optional<String> findPinByEmail(String email);
}
