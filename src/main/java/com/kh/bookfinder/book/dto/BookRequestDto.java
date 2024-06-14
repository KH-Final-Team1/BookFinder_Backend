package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.global.constants.Message;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequestDto {

  @NotNull(message = Message.UNSAVED_ISBN)
  private Long isbn;
  private String imageUrl;
  private String name;
  private String authors;
  private String publisher;
  private int publicationYear;
  private String classNo;
  private String className;
  private String description;

  public Book toEntity() {
    return Book.builder()
        .isbn(this.isbn)
        .imageUrl(this.imageUrl)
        .name(this.name)
        .authors(this.authors)
        .publisher(this.publisher)
        .publicationYear(this.publicationYear)
        .classNo(this.classNo)
        .className(this.className)
        .description(this.description)
        .approvalStatus(ApprovalStatus.WAIT)
        .build();
  }
}
