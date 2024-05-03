package com.kh.bookfinder.repository;

import com.kh.bookfinder.entity.BookTrade;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTradeRepository extends JpaRepository<BookTrade, Long> {

  ArrayList<BookTrade> findByBoroughId(Long boroughId);
}
