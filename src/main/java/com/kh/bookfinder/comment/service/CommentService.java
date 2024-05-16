package com.kh.bookfinder.comment.service;

import com.kh.bookfinder.comment.entity.Comment;
import com.kh.bookfinder.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;

  @Transactional
  public void saveComment(Comment comment) {
    commentRepository.save(comment);
  }
}
