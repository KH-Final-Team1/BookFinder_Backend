package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.book.entity.Book;
import lombok.Data;

@Data
public class BookRequestDto {

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
