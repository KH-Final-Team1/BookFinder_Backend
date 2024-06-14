package com.kh.bookfinder.book.entity;

import com.kh.bookfinder.book.enums.ApprovalStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

  @Id
  private Long isbn;
  private Integer additionSymbol;
  private String name;
  private String authors;
  private String publisher;
  private Integer publicationYear;
  private String classNo;
  private String className;
  private String description;
  private String imageUrl;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private ApprovalStatus approvalStatus = ApprovalStatus.WAIT;

}
