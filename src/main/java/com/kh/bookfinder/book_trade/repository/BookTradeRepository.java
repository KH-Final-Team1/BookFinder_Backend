package com.kh.bookfinder.book_trade.repository;

import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTradeRepository extends JpaRepository<BookTrade, Long> {

  List<BookTrade> findByBoroughIdAndDeleteYn(Long boroughId, Status deleteYn);

  List<BookTrade> findByUserId(Long userId);

  List<BookTrade> findByBoroughId(Long boroughId);
}
