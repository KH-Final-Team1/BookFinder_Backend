package com.kh.bookfinder.service;

import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.entity.Status;
import com.kh.bookfinder.repository.BookTradeRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookTradeService {

  @Autowired
  BookTradeRepository bookTradeRepository;

  public ArrayList<BookTrade> getBookTrades(Long boroughId) {
    return bookTradeRepository.findByBoroughIdAndDeleteYn(boroughId, Status.N);
  }

  @Transactional
  public void saveBookTrade(BookTrade bookTrade) {
    bookTradeRepository.save(bookTrade);
  }

  public Optional<BookTrade> findTrade(Long tradeId) {
    return bookTradeRepository.findById(tradeId);
  }

}
