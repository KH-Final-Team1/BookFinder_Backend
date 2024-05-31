package com.kh.bookfinder.helper;

import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.entity.Book;

public class MockBook {

  public static Book getMockBook() {
    return Book.builder()
        .isbn(1234567890123L)
        .name("test book name")
        .authors("test book authors")
        .publisher("test book publisher")
        .publicationYear(2024)
        .description("test book description")
        .imageUrl("test book image url")
        .approvalStatus(ApprovalStatus.APPROVE)
        .build();
  }

  public static Book getMockBook(ApprovalStatus status) {
    Book result = getMockBook();
    result.setApprovalStatus(status);
    return result;
  }
}
