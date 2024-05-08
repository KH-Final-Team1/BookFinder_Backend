package com.kh.bookfinder.service;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.CheckingVerificationDto;
import com.kh.bookfinder.entity.EmailAuth;
import com.kh.bookfinder.exception.InvalidFieldException;
import com.kh.bookfinder.repository.EmailAuthRepository;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailAuthService {

  private final JavaMailSender emailSender;
  private final EmailAuthRepository emailAuthRepository;

  public EmailAuth sendAuthCodeTo(String targetEmail) {
    EmailAuth emailAuth = EmailAuth
        .builder()
        .email(targetEmail)
        .build();
    emailAuthRepository.save(emailAuth);

    SimpleMailMessage emailForm = new SimpleMailMessage();
    emailForm.setTo(targetEmail);
    emailForm.setSubject(Message.MAIL_SUBJECT);
    emailForm.setText(emailAuth.getAuthCode());
    emailSender.send(emailForm);

    return emailAuth;
  }

  public String checkVerification(CheckingVerificationDto requestBody) throws JSONException {
    JSONObject decoded = decode(requestBody.getSigningToken());
    String signingTokenEmail = decoded.getString("email");
    String signingTokenAuthCode = decoded.getString("code");

    if (!signingTokenAuthCode.equals(requestBody.getAuthCode())) {
      throw new InvalidFieldException("signingToken", Message.INVALID_SIGNING_TOKEN);
    }

    EmailAuth target = this.emailAuthRepository.findByEmailAndAuthCode(signingTokenEmail, signingTokenAuthCode)
        .orElseThrow(() -> new InvalidFieldException("signingToken", Message.INVALID_SIGNING_TOKEN));

    if (LocalDateTime.now().isAfter(target.getExpiration())) {
      throw new InvalidFieldException("authCode", Message.EXPIRED_AUTH_CODE);
    }

    return target.generateSigningToken();
  }

  private JSONObject decode(String signingToken) throws JSONException {
    String decoded = new String(Base64.getDecoder().decode(signingToken), StandardCharsets.UTF_8);
    return new JSONObject(decoded);
  }
}
