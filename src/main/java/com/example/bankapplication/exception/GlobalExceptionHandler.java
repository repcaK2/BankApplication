package com.example.bankapplication.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.security.SignatureException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
		Map<String, Object> response = new HashMap<>();

		HttpStatus status = HttpStatus.UNAUTHORIZED;  // 401 Unauthorized

		response.put("timestamp", new Date());
		response.put("status", status.name());
		response.put("statusCode", status.value());
		response.put("series", status.series());
		response.put("isError", status.isError());
		response.put("isSuccessfull", status.is2xxSuccessful());
		response.put("message", "Unauthorized: Invalid or expired JWT token");

		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(SignatureException.class)
	public ResponseEntity<Map<String, Object>> handleSignatureException(SignatureException ex) {
		Map<String, Object> response = new HashMap<>();

		HttpStatus status = HttpStatus.UNAUTHORIZED;

		response.put("timestamp", new Date());
		response.put("status", status.name());
		response.put("statusCode", status.value());
		response.put("series", status.series());
		response.put("isError", status.isError());
		response.put("isSuccessfull", status.is2xxSuccessful());
		response.put("message", "Wrong token JWT");

		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {

		HttpStatus status = HttpStatus.BAD_REQUEST;

		String message;
		if (ex.getMessage() != null) {
			if (ex.getMessage().contains("_user_email_key")) {
				message = "Email already exists";
			} else if (ex.getMessage().contains("_user_phone_number_key")) {
				message = "Phone number already exists";
			} else {
				message = "Bad credentials";
			}
		} else {
			message = "Bad credentials";
		}

		Map<String, Object> response = new HashMap<>();
		response.put("status", status.name());
		response.put("statusCode", status.value());
		response.put("series", status.series());
		response.put("isError", status.isError());
		response.put("isSuccessfull", status.is2xxSuccessful());
		response.put("timeStamp", new Date());
		response.put("message", message);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
		Map<String, Object> response = new HashMap<>();

		HttpStatus status = (HttpStatus) ex.getStatusCode();

		response.put("status", status.name());
		response.put("statusCode", status.value());
		response.put("series", status.series());
		response.put("isError", status.isError());
		response.put("isSuccessfull", status.is2xxSuccessful());
		response.put("timeStamp", new Date());
		response.put("message", ex.getReason() != null ? ex.getReason() : "An internal error occurred");

		return new ResponseEntity<>(response, status);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
		Map<String, Object> response = new HashMap<>();

		HttpStatus status = HttpStatus.UNAUTHORIZED;

		response.put("timestamp", new Date());
		response.put("status", status.name());
		response.put("statusCode", status.value());
		response.put("series", status.series());
		response.put("isError", status.isError());
		response.put("isSuccessfull", status.is2xxSuccessful());
		response.put("message", "Invalid credentials provided");

		return new ResponseEntity<>(response, status);  // Zwrócenie odpowiedzi z kodem 401
	}

	//Global
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
		Map<String, Object> response = new HashMap<>();

		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

		response.put("timestamp", new Date());
		response.put("status", status.name());
		response.put("statusCode", status.value());
		response.put("series", status.series());
		response.put("isError", status.isError());
		response.put("isSuccessfull", status.is2xxSuccessful());
		response.put("message", ex.getMessage() != null ? ex.getMessage() : "An internal error occurred");  // Wiadomość błędu

		return new ResponseEntity<>(response, status);
	}
}
