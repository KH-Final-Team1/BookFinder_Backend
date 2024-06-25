package com.kh.bookfinder.book_trade.entity;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;

public enum Status {
  Y,
  N;

  public static Status fromStringIgnoreCase(String value) {
    for (Status status : Status.values()) {
      if (status.name().equalsIgnoreCase(value)) {
        return status;
      }
    }
    throw new InvalidFieldException("status", Message.INVALID_STATUS);
  }
}