package com.kh.bookfinder.comment.service;

import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.comment.dto.CommentRequestDto;
import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.comment.repository.CommentRepository;
import com.kh.bookfinder.global.constants.Message;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  public Comment findValidComment(Long commentId) {
    return commentRepository.findByIdAndDeleteYn(commentId, Status.N)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_COMMENT));
  }

  public ArrayList<Comment> getComments(Long tradeId) {
    return commentRepository.findByBookTradeIdAndDeleteYn(tradeId, Status.N);
  }

  @Transactional
  public void saveComment(Comment comment) {
    commentRepository.save(comment);
  }

  @Transactional
  public void updateComment(Long commentId, CommentRequestDto commentDto) {
    Comment comment = findValidComment(commentId);
    comment.setContent(commentDto.getContent());
    comment.setSecretYn(commentDto.getSecretYn());
    commentRepository.save(comment);
  }

}
