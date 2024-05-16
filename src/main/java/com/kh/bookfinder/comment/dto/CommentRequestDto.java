package com.kh.bookfinder.comment.dto;

import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.user.entity.User;
import lombok.Data;

@Data
public class CommentRequestDto {

  private Long userId;
  private String content;
  private Status secretYn;

  public Comment toEntity(BookTrade bookTrade, User user) {
    return Comment.builder()
        .bookTrade(bookTrade)
        .user(user)
        .content(this.content)
        .secretYn(this.secretYn)
        .build();
  }
}
