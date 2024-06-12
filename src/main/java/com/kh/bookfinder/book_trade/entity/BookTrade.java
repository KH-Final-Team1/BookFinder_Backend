package com.kh.bookfinder.book_trade.entity;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book_trade.dto.BookTradeDetailResponseDto;
import com.kh.bookfinder.book_trade.dto.BookTradeListResponseDto;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
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
@SuppressWarnings("unchecked")
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
  @Column(precision = 10, scale = 8)
  private BigDecimal latitude;
  @Column(precision = 11, scale = 8)
  private BigDecimal longitude;
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


  public <T> T toResponse(Class<T> responseType) {
    if (responseType == BookTradeListResponseDto.class) {
      return (T) this.toListResponseDto();
    } else if (responseType == BookTradeDetailResponseDto.class) {
      return (T) this.toDetailResponseDto();
    }
    return null;
  }

  private BookTradeDetailResponseDto toDetailResponseDto() {
    return BookTradeDetailResponseDto.builder()
        .id(this.id)
        .tradeType(this.tradeType)
        .tradeYn(this.tradeYn)
        .deleteYn(this.deleteYn)
        .content(this.content)
        .rentalCost(this.rentalCost)
        .longitude(this.longitude)
        .latitude(this.latitude)
        .limitedDate(this.limitedDate)
        .createDate(this.createDate)

        .isbn(this.book.getIsbn())
        .bookName(this.book.getName())
        .bookAuthors(this.book.getAuthors())
        .bookPublisher(this.book.getPublisher())
        .bookPublicationYear(this.book.getPublicationYear())
        .bookImageUrl(this.book.getImageUrl())
        .bookClassName(this.book.getClassName())
        .bookDescription(this.book.getDescription())

        .userId(this.user.getId())
        .userNickname(this.user.getNickname())
        .build();
  }

  private BookTradeListResponseDto toListResponseDto() {
    return BookTradeListResponseDto.builder()
        .id(this.id)
        .tradeType(this.tradeType)
        .tradeYn(this.tradeYn)
        .deleteYn(this.deleteYn)
        .rentalCost(this.rentalCost)
        .createDate(this.createDate)
        .bookName(this.book.getName())
        .bookAuthors(this.book.getAuthors())
        .bookPublicationYear(this.book.getPublicationYear())
        .bookImageUrl(this.book.getImageUrl())
        .userNickname(this.user.getNickname())
        .boroughName(this.borough.getName())
        .build();
  }

  public boolean isDeleted() {
    return this.deleteYn.equals(Status.Y);
  }
}