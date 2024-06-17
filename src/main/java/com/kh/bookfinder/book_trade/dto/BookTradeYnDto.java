package com.kh.bookfinder.book_trade.dto;

import com.kh.bookfinder.book_trade.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTradeYnDto {

  @NotNull
  private Status tradeYn;
}
