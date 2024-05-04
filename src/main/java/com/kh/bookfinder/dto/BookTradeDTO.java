package com.kh.bookfinder.dto;

import java.util.Date;
import lombok.Data;

@Data
public class BookTradeDTO {

  private String isbn;
  private String tradeType;
  private Integer rentalCost;
  private Date limitedDate;
  private String content;
  private float latitude;
  private float longitude;

}
