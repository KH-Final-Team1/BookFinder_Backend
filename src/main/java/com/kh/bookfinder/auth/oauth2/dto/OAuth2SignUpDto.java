package com.kh.bookfinder.auth.oauth2.dto;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.constants.Regexp;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuth2SignUpDto {

  @Pattern(regexp = Regexp.NICKNAME, message = Message.INVALID_NICKNAME)
  private String nickname;
  @NotNull(message = "올바른 주소를 입력하세요.")
  private String address;
  @Pattern(regexp = Regexp.PHONE, message = Message.INVALID_PHONE)
  private String phone;
}
