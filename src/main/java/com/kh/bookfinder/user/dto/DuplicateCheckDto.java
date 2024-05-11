package com.kh.bookfinder.user.dto;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.constants.Regexp;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import jakarta.validation.constraints.AssertTrue;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateCheckDto {

  private String field;
  private String value;

  @AssertTrue
  public boolean isValid() {
    if (!Arrays.asList("nickname", "email").contains(field)) {
      throw new InvalidFieldException("field", Message.INVALID_FIELD);
    }
    if (field.equals("email") && !isValidEmail()) {
      throw new InvalidFieldException("email", Message.INVALID_EMAIL);
    }
    if (field.equals("nickname") && !isValidNickname()) {
      throw new InvalidFieldException("nickname", Message.INVALID_NICKNAME);
    }

    return true;
  }

  public boolean isValidEmail() {
    return this.value.matches(Regexp.EMAIL);
  }

  public boolean isValidNickname() {
    return this.value.matches(Regexp.NICKNAME);
  }
}
