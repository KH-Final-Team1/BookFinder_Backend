package com.kh.bookfinder.auth.login.dto;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.constants.Regexp;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {

  @Pattern(regexp = Regexp.EMAIL, message = Message.INVALID_EMAIL)
  private String email;
  @Pattern(regexp = Regexp.PASSWORD, message = Message.INVALID_PASSWORD)
  private String password;
}
