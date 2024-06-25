package com.kh.bookfinder.user.enums;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;

public enum DuplicateCheckField {
  EMAIL,
  NICKNAME;

  public static DuplicateCheckField fromStringIgnoreCase(String value) {
    for (DuplicateCheckField field : DuplicateCheckField.values()) {
      if (field.name().equalsIgnoreCase(value)) {
        return field;
      }
    }
    throw new InvalidFieldException("field", Message.INVALID_FIELD);
  }
}
