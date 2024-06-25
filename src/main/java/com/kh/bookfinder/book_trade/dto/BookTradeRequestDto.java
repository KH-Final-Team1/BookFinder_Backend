package com.kh.bookfinder.book_trade.dto;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.TradeType;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.validation.ValidEnum;
import com.kh.bookfinder.user.entity.User;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookTradeRequestDto {

  @Range(min = 1000000000000L, max = 9999999999999L, message = Message.INVALID_ISBN_DIGITS)
  private Long isbn;
  @ValidEnum(enumClass = TradeType.class, message = Message.INVALID_TRADE_TYPE)
  private String tradeType;
  @NotNull(message = Message.INVALID_COST)
  @Range(max = 100000L, message = Message.INVALID_COST)
  private Integer rentalCost;
  @FutureOrPresent(message = Message.INVALID_LIMITED_DATE)
  private Date limitedDate;
  private String content;
  private BigDecimal latitude;
  private BigDecimal longitude;

  public BookTrade toEntity(User user, Book book) {
    return BookTrade.builder()
        .tradeType(TradeType.fromStringIgnoreCase(this.tradeType))
        .rentalCost(this.rentalCost)
        .limitedDate(this.limitedDate)
        .content(this.content)
        .latitude(this.latitude)
        .longitude(this.longitude)

        .user(user)
        .borough(user.getBorough())
        .book(book)
        .build();
  }
}