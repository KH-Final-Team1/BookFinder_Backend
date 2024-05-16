package com.kh.bookfinder.comment.repository;

import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.comment.entity.Comment;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  ArrayList<Comment> findByBookTradeIdAndDeleteYn(Long tradeId, Status deleteYn);
}
