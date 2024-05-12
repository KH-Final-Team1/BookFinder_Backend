package com.kh.bookfinder.book_trade.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.entity.TradeType;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
public class BookTradeListResponseDto {

  private Long id;
  private TradeType tradeType;
  private Status tradeYn;
  private Integer rentalCost;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private Date createdDate;
  private Map<String, Object> book = new HashMap<>();
  private Map<String, Object> user = new HashMap<>();
  private Map<String, Object> borough = new HashMap<>();

  @Builder
  public BookTradeListResponseDto(Long id, TradeType tradeType, Status tradeYn, Integer rentalCost, Date createdDate,
      String bookName, String bookAuthors, Integer bookPublicationYear, String bookImageUrl,
      String userNickname,
      String boroughName) {
    this.id = id;
    this.tradeType = tradeType;
    this.tradeYn = tradeYn;
    this.rentalCost = rentalCost;
    this.createdDate = createdDate;
    this.book.put("name", bookName);
    this.book.put("authors", bookAuthors);
    this.book.put("publicationYear", bookPublicationYear);
    this.book.put("imageUrl", bookImageUrl);
    this.user.put("nickname", userNickname);
    this.borough.put("name", boroughName);
  }
}
