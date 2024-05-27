package com.kh.bookfinder.book_trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.entity.TradeType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
public class BookTradeDetailResponseDto {

  private Long id;
  private Integer rentalCost;
  private String content;
  private BigDecimal longitude;
  private BigDecimal latitude;
  private TradeType tradeType;
  private Status tradeYn;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date limitedDate;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date createDate;
  private Map<String, Object> user = new HashMap<>();
  private Map<String, Object> book = new HashMap<>();

  @Builder
  public BookTradeDetailResponseDto(Long id, TradeType tradeType, Status tradeYn, String content,
      Integer rentalCost, BigDecimal longitude, BigDecimal latitude, Date createDate, Date limitedDate,
      Long isbn, String bookName, String bookAuthors, String bookPublisher, String bookImageUrl,
      String bookDescription, String bookClassName, Integer bookPublicationYear,
      Long userId, String userNickname) {
    this.id = id;
    this.tradeType = tradeType;
    this.tradeYn = tradeYn;
    this.content = content;
    this.rentalCost = rentalCost;
    this.longitude = longitude;
    this.latitude = latitude;
    this.limitedDate = limitedDate;
    this.createDate = createDate;

    this.book.put("isbn", isbn);
    this.book.put("name", bookName);
    this.book.put("authors", bookAuthors);
    this.book.put("publisher", bookPublisher);
    this.book.put("publicationYear", bookPublicationYear);
    this.book.put("imageUrl", bookImageUrl);
    this.book.put("className", bookClassName);
    this.book.put("description", bookDescription);

    this.user.put("tradeWriterId", userId);
    this.user.put("nickname", userNickname);
  }
}
