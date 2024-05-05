package com.kh.bookfinder.dto;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.constants.Regexp;
import com.kh.bookfinder.exception.InvalidFieldException;
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

  private String field;
  private String value;

  @AssertTrue
  public boolean isValid() {
    if (!field.equals("email")) {
      throw new InvalidFieldException("field", Message.INVALID_FIELD);
    }
    if (!isValidEmail()) {
      throw new InvalidFieldException("email", Message.INVALID_EMAIL);
    }

    return true;
  }

  public boolean isValidEmail() {
    return this.value.matches(Regexp.EMAIL);
  }
}
