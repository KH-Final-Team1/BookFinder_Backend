package com.kh.bookfinder.comment.controller;

import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.comment.dto.CommentRequestDto;
import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.comment.service.CommentService;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/comments")
public class CommentController {

  private final CommentService commentService;
  private final BookTradeService bookTradeService;
  private final UserService userService;

  @PostMapping("/{tradeId}")
  public ResponseEntity<Comment> createComment(@PathVariable(name = "tradeId") Long tradeId,
      @RequestBody CommentRequestDto commentDto) {
    BookTrade bookTrade = bookTradeService.findTrade(tradeId);
    User user = userService.findUser(commentDto.getUserId());
    Comment comment = commentDto.toEntity(bookTrade, user);

    commentService.saveComment(comment);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}