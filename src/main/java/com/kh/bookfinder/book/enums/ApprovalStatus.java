package com.kh.bookfinder.book.enums;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;

public enum ApprovalStatus {
  REJECT,
  WAIT,
  APPROVE;

  public static ApprovalStatus fromStringIgnoreCase(String value) {
    if (value == null) {
      return null;
    }
    for (ApprovalStatus status : ApprovalStatus.values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new InvalidFieldException("status", Message.INVALID_APPROVAL_STATUS);
  }
}