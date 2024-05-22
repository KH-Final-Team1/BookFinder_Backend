package com.kh.bookfinder.comment.controller;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.comment.dto.CommentRequestDto;
import com.kh.bookfinder.comment.dto.CommentResponseDto;
import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.comment.service.CommentService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.service.UserService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
      @RequestBody @Valid CommentRequestDto commentDto) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    SecurityUserDetails principal = (SecurityUserDetails) authentication.getPrincipal();
    String email = principal.getUsername();

    commentService.saveComment(email, tradeId, commentDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/{commentId}")
  public ResponseEntity<Map<String, String>> updateComment(@PathVariable(name = "commentId") Long commentId,
      @RequestBody @Valid CommentRequestDto commentDto) {
    commentService.updateComment(commentId, commentDto);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_UPDATE));
  }

  @DeleteMapping("{commentId}")
  public ResponseEntity<Map<String, String>> updateComment(@PathVariable(name = "commentId") Long commentId) {
    commentService.deleteComment(commentId);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_DELETE));
  }
}