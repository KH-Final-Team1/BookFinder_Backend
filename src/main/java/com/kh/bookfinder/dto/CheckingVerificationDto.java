package com.kh.bookfinder.dto;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.constants.Regexp;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckingVerificationDto {

  @Pattern(regexp = Regexp.AUTH_CODE, message = Message.INVALID_AUTH_CODE)
  private String authCode;
  private String signingToken;
}
