package com.kh.bookfinder.repository;

import com.kh.bookfinder.entity.BookTrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTradeRepository extends JpaRepository<BookTrade, Long> {

}
