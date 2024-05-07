package com.kh.bookfinder.service;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.entity.Status;
import com.kh.bookfinder.repository.BookTradeRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookTradeService {

  @Autowired
  BookTradeRepository bookTradeRepository;

  public Optional<BookTrade> findTrade(Long tradeId) {
    return bookTradeRepository.findById(tradeId);
  }

  public BookTrade getBookTrade(Long tradeId) {
    BookTrade bookTrade = bookTradeRepository.findById(tradeId)
        .orElseThrow(() -> new ResourceNotFoundException(Message.INVALID_TRADE));
    if (bookTrade.getDeleteYn().equals(Status.Y)) {
      throw new ResourceNotFoundException(Message.DELETED_TRADE);
    }

    return bookTrade;
  }

  public ArrayList<BookTrade> getBookTrades(Long boroughId) {
    return bookTradeRepository.findByBoroughIdAndDeleteYn(boroughId, Status.N);
  }

  @Transactional
  public void saveBookTrade(BookTrade bookTrade) {
    bookTradeRepository.save(bookTrade);
  }

  public void deleteTrade(Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId)
        .orElseThrow(() -> new ResourceNotFoundException(Message.INVALID_TRADE));
    bookTrade.setDeleteYn(Status.Y);
    bookTradeRepository.save(bookTrade);
  }

}
