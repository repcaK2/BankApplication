package com.example.bankapplication.blik;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blik_codes")
@Builder
public class Blik {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NaturalId(mutable = false)
	private String blikCode;
	private String creatorEmail;
	private Date expirationTime;

	@Override
	public String toString() {
		return "Blik{" +
				"blikCode='" + blikCode + '\'' +
				", creatorEmail='" + creatorEmail + '\'' +
				", expirationTime=" + expirationTime +
				'}';
	}
}
