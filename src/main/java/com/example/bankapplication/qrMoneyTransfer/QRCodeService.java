package com.example.bankapplication.qrMoneyTransfer;

import com.example.bankapplication.user.User;
import com.example.bankapplication.user.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
@Transactional
@RequiredArgsConstructor
public class QRCodeService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public String generateQRCodeTransaction(
			String ownerEmail,
			BigDecimal amount,
			String pin
	) throws WriterException, IOException {

		User foundUser = userRepository.findByEmail(ownerEmail)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + ownerEmail));

		String storedPin = foundUser.getPin();

		// Check if pins match
		if (!passwordEncoder.matches(pin, storedPin)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PIN does not match");
		}

		String encodedPin = passwordEncoder.encode(pin);

		String userHome = System.getProperty("user.home");
		String qrCodePath = "C:\\Users\\ADMIN\\Desktop\\qr";
		String qrCodeName = qrCodePath + "\\MoneyTransfer" + "-QRCODE.png";
		var qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(
				"Owner Email: " + ownerEmail +
				"Amount: " + amount + " " +
				encodedPin,
				BarcodeFormat.QR_CODE, 400, 400
		);
		Path path = FileSystems.getDefault().getPath(qrCodeName);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
		return "created";
	}
}
