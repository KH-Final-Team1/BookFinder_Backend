package com.kh.bookfinder.user.dto;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.constants.Regexp;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignUpDto {

  @Pattern(regexp = Regexp.EMAIL, message = Message.INVALID_EMAIL)
  private String email;
  @Pattern(regexp = Regexp.PASSWORD, message = Message.INVALID_PASSWORD)
  private String password;
  private String passwordConfirm;
  @Pattern(regexp = Regexp.NICKNAME, message = Message.INVALID_NICKNAME)
  private String nickname;
  @NotNull(message = "올바른 주소를 입력하세요.")
  private String address;
  @Pattern(regexp = Regexp.PHONE, message = Message.INVALID_PHONE)
  private String phone;

  public boolean equalsPassword() {
    return this.password.equals(this.passwordConfirm);
  }

  public User toEntity(PasswordEncoder passwordEncoder) {
    return User.builder()
        .email(this.email)
        .nickname(this.nickname)
        .password(passwordEncoder.encode(this.password))
        .address(this.address)
        .phone(this.phone)
        .role(UserRole.ROLE_USER)
        .build();
  }
}