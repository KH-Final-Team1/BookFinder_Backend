package com.kh.bookfinder.service;

import com.kh.bookfinder.entity.EmailAuth;
import com.kh.bookfinder.repository.EmailAuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailAuthService {

  private final static String MAIL_SUBJECT = "북적북적에서 보내는 이메일 인증 코드입니다.";
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
    emailForm.setSubject(MAIL_SUBJECT);
    emailForm.setText(emailAuth.getAuthCode());
    emailSender.send(emailForm);

    return emailAuth;
  }
}
