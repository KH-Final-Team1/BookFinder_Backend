package com.kh.bookfinder.book_trade.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Borough {

  @Transient
  private static int MIN_BOROUGH = 1;
  @Transient
  private static int MAX_BOROUGH = 25;

  @Id
  @Column(name = "BOROUGH_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(unique = true, nullable = false)
  private String name;

  public static boolean isValid(Long id) {
    return id >= MIN_BOROUGH && id <= MAX_BOROUGH;
  }
}
