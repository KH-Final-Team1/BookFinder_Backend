package com.kh.bookfinder.book_trade.dto;

import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.validation.ValidEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTradeYnDto {

  @ValidEnum(enumClass = Status.class, message = Message.INVALID_STATUS)
  private String tradeYn;
}
