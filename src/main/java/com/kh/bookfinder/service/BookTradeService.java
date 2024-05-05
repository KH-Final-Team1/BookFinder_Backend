package com.kh.bookfinder.service;

import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.entity.Status;
import com.kh.bookfinder.repository.BookTradeRepository;
import java.util.ArrayList;
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
  public void createBookTrade(BookTrade bookTrade) {
    bookTradeRepository.save(bookTrade);
  }

}
