package com.kh.bookfinder.exception;

import lombok.Getter;

@Getter
public class InvalidFieldException extends RuntimeException {

  private String field;

  public InvalidFieldException(String field, String message) {
    super(message);
    this.field = field;
  }

  public InvalidFieldException() {
    super();
  }

  public InvalidFieldException(String field, String message, Throwable cause) {
    super(message, cause);
    this.field = field;
  }

  public InvalidFieldException(Throwable cause) {
    super(cause);
  }
}
