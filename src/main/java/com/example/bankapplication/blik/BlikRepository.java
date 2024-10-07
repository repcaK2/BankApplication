package com.example.bankapplication.blik;

import com.example.bankapplication.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlikRepository extends JpaRepository<Blik, Long> {

	Optional<Blik> findBlikByCreatorEmail(String email);

	boolean existsBlikByBlikCode(String blikCode);

	Optional<Blik> findByBlikCode(String blikCode);


}
