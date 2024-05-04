package com.kh.bookfinder.dto;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.entity.TradeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class BookTradeDTO {

  private String isbn;
  private TradeType tradeType;
  @NotNull(message = Message.NULLABLE_COST)
  @Min(value = 0, message = Message.INVALID_COST)
  @Max(value = 100000, message = Message.INVALID_COST)
  private Integer rentalCost;
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private Date limitedDate;
  private String content;
  private float latitude;
  private float longitude;

}