package com.kh.bookfinder.user.dto;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.constants.Regexp;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import com.kh.bookfinder.global.validation.ValidEnum;
import com.kh.bookfinder.user.enums.DuplicateCheckField;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateCheckDto {

  @ValidEnum(enumClass = DuplicateCheckField.class)
  private String field;
  private String value;

  @AssertTrue
  public boolean isValid() {
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
