package com.kh.bookfinder.repository;

import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.entity.Status;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTradeRepository extends JpaRepository<BookTrade, Long> {

  ArrayList<BookTrade> findByBoroughIdAndDeleteYn(Long boroughId, Status deleteYn);
}
