package com.example.bankapplication.blik;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	private double requestedFunds;
	private String status;
}
