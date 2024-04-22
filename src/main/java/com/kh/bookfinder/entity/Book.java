package com.kh.bookfinder.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
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
  private int additionSymbol;
  private String name;
  private String authors;
  private String publisher;
  private int publicationYear;
  @Temporal(TemporalType.DATE)
  private LocalDate publicationDate;
  private String classNo;
  private String className;
  private String description;
  private String imageUrl;
}
