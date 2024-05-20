package com.kh.bookfinder.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.kh.bookfinder.book_trade.entity.Status;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
public class CommentResponseDto {

  private Long id;
  private String content;
  private Status secretYn;
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private Date createDate;
  private Map<String, Object> user = new HashMap<>();

  @Builder
  public CommentResponseDto(Long id, String content, Status secretYn, Date createDate, String nickname) {
    this.id = id;
    this.content = content;
    this.secretYn = secretYn;
    this.createDate = createDate;
    this.user.put("nickname", nickname);
  }
}
