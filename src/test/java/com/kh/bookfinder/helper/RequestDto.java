package com.kh.bookfinder.helper;

import com.kh.bookfinder.book.dto.BookCreateRequestDto;
import com.kh.bookfinder.book.dto.BookListRequestDto;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.entity.TradeType;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class RequestDto {

  public static BookTradeRequestDto baseBookTradeRequestDto() {
    return BookTradeRequestDto.builder()
        .isbn(9788960773424L)
        .tradeType(TradeType.BORROW)
        .rentalCost(1200)
        .limitedDate(Date.valueOf(LocalDate.now().plusDays(7)))
        .content("valid test content, 테스트 컨텐츠")
        .latitude(BigDecimal.valueOf(12.3))
        .longitude(BigDecimal.valueOf(23.4))
        .build();
  }

  public static BookTradeRequestDto updateBookTradeRequestDto() {
    return BookTradeRequestDto.builder()
        .tradeType(TradeType.BORROW)
        .rentalCost(1500)
        .limitedDate(Date.valueOf(LocalDate.now().plusDays(14)))
        .content("valid update test content, 업데이트 컨텐츠")
        .latitude(BigDecimal.valueOf(12.3))
        .longitude(BigDecimal.valueOf(23.4))
        .build();
  }

  public static BookListRequestDto baseBookSearchRequestDto() {
    return BookListRequestDto.builder()
        .filter("name")
        .keyword("")
        .status("approve")
        .build();
  }

  public static BookCreateRequestDto baseBookCreateRequestDto() {
    return BookCreateRequestDto.builder()
        .isbn(1234567891011L)
        .imageUrl("test image url")
        .name("test name")
        .authors("test authors")
        .publisher("test publisher")
        .publicationYear(2024)
        .classNo("test class no")
        .className("test class name")
        .description("test description")
        .build();
  }
}
