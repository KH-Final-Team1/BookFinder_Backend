package com.kh.bookfinder.helper;

import com.kh.bookfinder.book.entity.ApprovalStatus;
import com.kh.bookfinder.book.entity.Book;
import java.util.ArrayList;
import java.util.List;

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

  public static List<Book> list(int count) {
    List<Book> result = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      result.add(
          Book.builder()
              .isbn(1234567891011L + i)
              .name("test book name " + i)
              .authors("test book authors " + i)
              .publisher("test book publisher " + i)
              .publicationYear(2024)
              .description("test book description " + i)
              .imageUrl("test book image url " + i)
              .approvalStatus(ApprovalStatus.APPROVE)
              .build()
      );
    }
    return result;
  }
}
