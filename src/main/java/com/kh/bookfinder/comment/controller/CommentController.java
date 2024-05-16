package com.kh.bookfinder.comment.controller;

import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.comment.dto.CommentRequestDto;
import com.kh.bookfinder.comment.dto.CommentResponseDto;
import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.comment.service.CommentService;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/{tradeId}")
  public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable(name = "tradeId") Long tradeId) {
    bookTradeService.findTrade(tradeId);
    ArrayList<Comment> commentList = commentService.getComments(tradeId);

    List<CommentResponseDto> response = commentList.stream().map(
        x -> x.toResponse(CommentResponseDto.class)
    ).collect(Collectors.toList());

    return ResponseEntity.ok().body(response);
  }

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