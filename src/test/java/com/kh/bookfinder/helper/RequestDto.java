package com.kh.bookfinder.helper;

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
}
