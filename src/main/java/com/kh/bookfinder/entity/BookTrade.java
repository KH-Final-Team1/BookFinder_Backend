package com.kh.bookfinder.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookTrade {

  @Id
  @Column(name = "TRADE_ID")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne
  @JoinColumn(name = "USER_ID")
  private User user;
  @ManyToOne
  @JoinColumn(name = "ISBN")
  private Book book;
  @ManyToOne
  @JoinColumn(name = "BOROUGH_ID")
  private Borough borough;
  @Enumerated(EnumType.STRING)
  private TradeType tradeType;
  @Column(nullable = false)
  private int rentalCost;
  private String content;
  private float latitude;
  private float longitude;
  private Date limitedDate;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Status tradeYn = Status.N;
  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Status deleteYn = Status.N;
  @CreationTimestamp
  private Date createDate;
  @UpdateTimestamp
  private Date updateDate;

}