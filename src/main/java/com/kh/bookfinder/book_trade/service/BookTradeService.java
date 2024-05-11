package com.kh.bookfinder.book_trade.service;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookTradeService {

  private final BookTradeRepository bookTradeRepository;

  public BookTrade findTrade(Long tradeId) {
    return bookTradeRepository.findById(tradeId)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_TRADE));
  }

  public BookTrade getBookTrade(Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId);
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

  @Transactional
  public void deleteTrade(Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId);
    bookTrade.setDeleteYn(Status.Y);
  }

}
