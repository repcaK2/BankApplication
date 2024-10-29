package com.example.bankapplication.blik;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "waiting_blikcodes")
public class RequestBlik {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String blikCode;
	private String description;
	private String requesterEmail;
	private BigDecimal requestedFunds;
	private String status;
}
