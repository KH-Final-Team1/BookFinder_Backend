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
  private Status deleteYn;
  private Integer rentalCost;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  private Date createDate;
  private Map<String, Object> book = new HashMap<>();
  private Map<String, Object> user = new HashMap<>();
  private Map<String, Object> borough = new HashMap<>();

  @Builder
  public BookTradeListResponseDto(Long id, TradeType tradeType, Status tradeYn, Status deleteYn, Integer rentalCost,
      Date createDate,
      String bookName, String bookAuthors, Integer bookPublicationYear, String bookImageUrl,
      String userNickname,
      String boroughName) {
    this.id = id;
    this.tradeType = tradeType;
    this.deleteYn = deleteYn;
    this.tradeYn = tradeYn;
    this.rentalCost = rentalCost;
    this.createDate = createDate;
    this.book.put("name", bookName);
    this.book.put("authors", bookAuthors);
    this.book.put("publicationYear", bookPublicationYear);
    this.book.put("imageUrl", bookImageUrl);
    this.user.put("nickname", userNickname);
    this.borough.put("name", boroughName);
  }
}
