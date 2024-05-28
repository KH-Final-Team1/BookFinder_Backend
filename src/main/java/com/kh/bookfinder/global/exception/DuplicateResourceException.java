package com.kh.bookfinder.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {

  public DuplicateResourceException() {
    super();
  }

  public DuplicateResourceException(String message) {
    super(message);
  }

  public DuplicateResourceException(String message, Throwable cause) {
    super(message, cause);
  }
}
