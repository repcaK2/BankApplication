package com.example.bankapplication.blik;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestBlikRepository extends JpaRepository<RequestBlik, Long> {
	Optional<RequestBlik> findByBlikCode(String blikCode);
}
