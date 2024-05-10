package com.kh.bookfinder.dto;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.entity.TradeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class BookTradeDTO {

  @Min(value = 1000000000000L, message = Message.INVALID_ISBN_DIGITS)
  @Max(value = 9999999999999L, message = Message.INVALID_ISBN_DIGITS)
  private Long isbn;
  private TradeType tradeType;
  @NotNull(message = Message.NULLABLE_COST)
  @Min(value = 0, message = Message.INVALID_COST)
  @Max(value = 100000, message = Message.INVALID_COST)
  private Integer rentalCost;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date limitedDate;
  private String content;
  private BigDecimal latitude;
  private BigDecimal longitude;

}