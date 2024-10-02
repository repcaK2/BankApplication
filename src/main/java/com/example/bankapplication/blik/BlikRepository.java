package com.example.bankapplication.blik;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlikRepository extends JpaRepository<Blik, Long> {

	boolean existsBlikByBlikCode(String blikCode);

	Optional<Blik> findByBlikCode(String blikCode);


}
