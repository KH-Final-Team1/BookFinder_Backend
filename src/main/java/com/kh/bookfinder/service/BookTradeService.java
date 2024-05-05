package com.kh.bookfinder.service;

import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.repository.BookTradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookTradeService {

  @Autowired
  BookTradeRepository bookTradeRepository;

  @Transactional
  public void createBookTrade(BookTrade bookTrade) {
    bookTradeRepository.save(bookTrade);
  }

}
