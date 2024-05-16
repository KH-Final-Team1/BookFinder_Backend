package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.global.constants.Message;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequestDto {

  @NotNull(message = Message.UNSAVED_ISBN)
  private Long isbn;
  private String name;
  private String authors;
  private String publisher;
  private int publicationYear;

  public Book toEntity() {
    return Book.builder()
        .isbn(this.isbn)
        .name(this.name)
        .authors(this.authors)
        .publisher(this.publisher)
        .publicationYear(this.publicationYear)
        .approvalStatus("WAIT")
        .build();
  }
}
