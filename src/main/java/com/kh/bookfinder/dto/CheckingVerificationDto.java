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

  @AssertTrue(message = Message.INVALID_SIGNING_TOKEN)
  private boolean isSigningToken() {
    String decoded = new String(Base64.getDecoder().decode(signingToken), StandardCharsets.UTF_8);
    try {
      JSONObject result = new JSONObject(decoded);
      result.get("email");
      result.get("code");
      return true;
    } catch (JSONException e) {
      return false;
    }
  }
}
