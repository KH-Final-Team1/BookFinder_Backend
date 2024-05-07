package com.kh.bookfinder.dto;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.constants.Regexp;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendingEmailAuthDto {

  @Pattern(regexp = Regexp.EMAIL, message = Message.INVALID_EMAIL)
  private String email;
}
