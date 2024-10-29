package com.example.bankapplication.qrMoneyTransfer;

import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qr")
public class QRCodeController {

	private final QRCodeService qrCodeService;

	@PostMapping("/generateTransaction")
	public ResponseEntity<String> generateQRCodeTransaction(
			@RequestBody QRCodeTransferRequest qrCodeTransferRequest,
			Principal principal
	) throws IOException, WriterException {
		String ownerEmail = principal.getName();
		BigDecimal amount = qrCodeTransferRequest.getAmount();
		String pin = qrCodeTransferRequest.getPin();
		qrCodeService.generateQRCodeTransaction(ownerEmail, amount, pin);
		return ResponseEntity.ok().body("QR Code Created");
	}
}
