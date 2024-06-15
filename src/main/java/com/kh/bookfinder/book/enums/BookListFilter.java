package com.kh.bookfinder.book.enums;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;

public enum BookListFilter {
  NAME,
  AUTHORS,
  PUBLISHER;

  public static BookListFilter fromStringIgnoreCase(String value) {
    for (BookListFilter filter : BookListFilter.values()) {
      if (filter.name().equalsIgnoreCase(value)) {
        return filter;
      }
    }
    throw new InvalidFieldException("filter", Message.INVALID_FILTER);
  }
}
