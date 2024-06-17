package com.kh.bookfinder.book.dto;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.global.constants.Message;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateRequestDto {

  @Range(min = 1000000000000L, max = 9999999999999L, message = Message.INVALID_ISBN_DIGITS)
  @NotNull
  private Long isbn;
  @NotNull
  private String imageUrl;
  @NotNull
  private String name;
  @NotNull
  private String authors;
  @NotNull
  private String publisher;
  @NotNull
  private Integer publicationYear;
  @NotNull
  private String classNo;
  @NotNull
  private String className;
  @NotNull
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
