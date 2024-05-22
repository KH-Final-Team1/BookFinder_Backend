package com.kh.bookfinder.comment.service;

import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.comment.dto.CommentRequestDto;
import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.comment.repository.CommentRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.UnauthorizedException;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.service.UserService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final UserService userService;
  private final BookTradeService bookTradeService;

  public Comment findComment(Long commentId) {
    return commentRepository.findById(commentId)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_COMMENT));
  }

  public Comment findValidComment(Long commentId) {
    return commentRepository.findByIdAndDeleteYn(commentId, Status.N)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_COMMENT));
  }

  public ArrayList<Comment> getComments(Long tradeId) {
    return commentRepository.findByBookTradeIdAndDeleteYn(tradeId, Status.N);
  }

  @Transactional
  public void saveComment(String email, Long tradeId, CommentRequestDto commentDto) {
    User user = userService.findUser(email);
    BookTrade bookTrade = bookTradeService.findTrade(tradeId);
    Comment comment = commentDto.toEntity(bookTrade, user);
    commentRepository.save(comment);
  }

  @Transactional
  public void updateComment(String email, Long commentId, CommentRequestDto commentDto) {
    User user = userService.findUser(email);
    Comment comment = findValidComment(commentId);
    if (comment.getUser().getId().equals(user.getId())) {
      comment.setContent(commentDto.getContent());
      comment.setSecretYn(commentDto.getSecretYn());
      commentRepository.save(comment);
    } else {
      throw new UnauthorizedException(Message.NOT_AUTHORIZED);
    }
  }

  @Transactional
  public void deleteComment(String email, Long commentId) {
    User user = userService.findUser(email);
    Comment comment = findValidComment(commentId);
    if (comment.getUser().getId().equals(user.getId())) {
      comment.setDeleteYn(Status.Y);
    } else {
      throw new UnauthorizedException(Message.NOT_AUTHORIZED);
    }
  }

}
