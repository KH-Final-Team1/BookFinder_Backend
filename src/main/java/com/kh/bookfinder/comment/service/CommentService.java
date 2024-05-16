package com.kh.bookfinder.comment.service;

import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.comment.repository.CommentRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  public ArrayList<Comment> getComments(Long tradeId) {
    return commentRepository.findByBookTradeIdAndDeleteYn(tradeId, Status.N);
  }

  @Transactional
  public void saveComment(Comment comment) {
    commentRepository.save(comment);
  }
}
