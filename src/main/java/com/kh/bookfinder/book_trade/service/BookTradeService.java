package com.kh.bookfinder.book_trade.service;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.service.BookService;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.service.UserService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookTradeService {

  private final BookTradeRepository bookTradeRepository;
  private final UserService userService;
  private final BookService bookService;

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
  public void saveBookTrade(String email, BookTradeRequestDto tradeDto) {
    User user = userService.findUser(email);
    Book book = bookService.findApprovedBook(tradeDto.getIsbn());
    BookTrade bookTrade = tradeDto.toEntity(book);
    bookTrade.setUser(user);
    bookTradeRepository.save(bookTrade);
  }

  @Transactional
  public void updateBookTrade(Long tradeId, BookTradeRequestDto tradeDto) {
    BookTrade bookTrade = findTrade(tradeId);
    bookTrade.setRentalCost(tradeDto.getRentalCost());
    bookTrade.setLimitedDate(tradeDto.getLimitedDate());
    bookTrade.setContent(tradeDto.getContent());
    bookTrade.setLatitude(tradeDto.getLatitude());
    bookTrade.setLongitude(tradeDto.getLongitude());
  }

  @Transactional
  public void deleteTrade(Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId);
    bookTrade.setDeleteYn(Status.Y);
  }

  @Transactional
  public void changeTrade(Long tradeId, Status tradeYn) {
    BookTrade bookTrade = findTrade(tradeId);
    bookTrade.setTradeYn(tradeYn);
  }
}
