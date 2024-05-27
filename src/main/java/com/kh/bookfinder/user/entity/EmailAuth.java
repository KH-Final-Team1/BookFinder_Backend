package com.kh.bookfinder.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "email_auth",
    uniqueConstraints = @UniqueConstraint(columnNames = {"auth_code", "email"}))
public class EmailAuth implements Serializable {

  private final static String ALPHABET_AND_NUMBER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private final static int AUTH_CODE_MAX_LENGTH = 8;
  private final static int DEFAULT_EXPIRATION_MINUTES = 5;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long authId;
  @Default
  private String authCode = generateAuthCode();
  private String email;
  @Default
  private LocalDateTime expiration = LocalDateTime.now().plusMinutes(DEFAULT_EXPIRATION_MINUTES);

  private static String generateAuthCode() {
    Random random = new Random();
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < AUTH_CODE_MAX_LENGTH; i++) {
      int index = random.nextInt(ALPHABET_AND_NUMBER.length());
      result.append(ALPHABET_AND_NUMBER.charAt(index));
    }
    return result.toString();
  }
}
