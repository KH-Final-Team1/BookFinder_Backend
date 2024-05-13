package com.kh.bookfinder.book_trade.dto;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.TradeType;
import com.kh.bookfinder.global.constants.Message;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class BookTradeRequestDto {

  @Min(value = 1000000000000L, message = Message.INVALID_ISBN_DIGITS)
  @Max(value = 9999999999999L, message = Message.INVALID_ISBN_DIGITS)
  private Long isbn;
  private TradeType tradeType;
  @NotNull(message = Message.NULLABLE_COST)
  @Min(value = 0, message = Message.INVALID_COST)
  @Max(value = 100000, message = Message.INVALID_COST)
  private Integer rentalCost;
  private Date limitedDate;
  private String content;
  private BigDecimal latitude;
  private BigDecimal longitude;

  public BookTrade toEntity(Book book) {
    return BookTrade.builder()
        .book(book)
        .tradeType(this.tradeType)
        .rentalCost(this.rentalCost)
        .limitedDate(this.limitedDate)
        .content(this.content)
        .latitude(this.latitude)
        .longitude(this.longitude)
        .build();
  }
}