package com.kh.bookfinder.user.service;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.DuplicateResourceException;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import com.kh.bookfinder.user.dto.CheckingVerificationDto;
import com.kh.bookfinder.user.entity.EmailAuth;
import com.kh.bookfinder.user.repository.EmailAuthRepository;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailAuthService {

  private final JavaMailSender emailSender;
  private final EmailAuthRepository emailAuthRepository;
  private final UserRepository userRepository;

  public EmailAuth sendAuthCodeTo(String targetEmail) {
    if (alreadyExistEmail(targetEmail)) {
      throw new DuplicateResourceException(Message.DUPLICATE_NICKNAME);
    }

    EmailAuth emailAuth = EmailAuth
        .builder()
        .email(targetEmail)
        .build();

    SimpleMailMessage emailForm = new SimpleMailMessage();
    emailForm.setTo(targetEmail);
    emailForm.setSubject(Message.MAIL_SUBJECT);
    emailForm.setText(emailAuth.getAuthCode());
    emailSender.send(emailForm);

    emailAuthRepository.save(emailAuth);

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
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_EMAIL_AUTH));

    if (LocalDateTime.now().isAfter(target.getExpiration())) {
      throw new InvalidFieldException("authCode", Message.EXPIRED_AUTH_CODE);
    }

    return generateSigningToken(target);
  }

  private JSONObject decode(String signingToken) throws JSONException {
    String decoded = new String(Base64.getDecoder().decode(signingToken), StandardCharsets.UTF_8);
    return new JSONObject(decoded);
  }

  public String generateSigningToken(EmailAuth emailAuth) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("email", emailAuth.getEmail());
    jsonObject.put("code", emailAuth.getAuthCode());

    return Base64.getEncoder().encodeToString(jsonObject.toString().getBytes(StandardCharsets.UTF_8));
  }

  private boolean alreadyExistEmail(String email) {
    return userRepository.findByEmail(email).isPresent();
  }
}
