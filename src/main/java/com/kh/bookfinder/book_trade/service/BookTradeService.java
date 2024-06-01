package com.kh.bookfinder.book_trade.service;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.service.BookService;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookTradeService {

  private final BookTradeRepository bookTradeRepository;
  private final BookService bookService;

  public BookTrade findTrade(Long tradeId) {
    return bookTradeRepository.findById(tradeId)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_TRADE));
  }

  public BookTrade getBookTrade(User serviceUser, Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId);
    if (serviceUser.isAdmin()) {
      return bookTrade;
    }
    if (bookTrade.isDeleted()) {
      throw new ResourceNotFoundException(Message.DELETED_TRADE);
    }
    if (!serviceUser.getBorough().equals(bookTrade.getBorough())) {
      throw new AccessDeniedException(Message.FORBIDDEN_BOOK_TRADES_READ);
    }

    return bookTrade;
  }

  public BookTrade getBookTrade(Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId);
    if (bookTrade.isDeleted()) {
      throw new ResourceNotFoundException(Message.DELETED_TRADE);
    }

    return bookTrade;
  }

  public List<BookTrade> getBookTradesByBoroughId(User serviceUser, Long boroughId) {
    if (serviceUser.isAdmin()) {
      return bookTradeRepository.findByBoroughId(boroughId);
    }

    if (serviceUser.getRole() == UserRole.ROLE_USER && !serviceUser.getBorough().getId().equals(boroughId)) {
      throw new AccessDeniedException(Message.FORBIDDEN_BOOK_TRADES_READ);
    }

    return bookTradeRepository.findByBoroughIdAndDeleteYn(boroughId, Status.N);
  }

  public List<BookTrade> getBookTradesByUserId(Long userId) {
    return bookTradeRepository.findByUserId(userId);
  }

  @Transactional
  public void saveBookTrade(User serviceUser, BookTradeRequestDto tradeDto) {
    Book book = bookService.findApprovedBook(tradeDto.getIsbn());
    BookTrade bookTrade = tradeDto.toEntity(serviceUser, book);
    bookTradeRepository.save(bookTrade);
  }

  @Transactional
  public void updateBookTrade(User user, Long tradeId, BookTradeRequestDto tradeDto) {
    BookTrade bookTrade = getBookTrade(tradeId);
    if (!user.equals(bookTrade.getUser())) {
      throw new AccessDeniedException(Message.FORBIDDEN_BOOK_TRADES_UPDATE);
    }

    bookTrade.setRentalCost(tradeDto.getRentalCost());
    bookTrade.setLimitedDate(tradeDto.getLimitedDate());
    bookTrade.setContent(tradeDto.getContent());
    bookTrade.setLatitude(tradeDto.getLatitude());
    bookTrade.setLongitude(tradeDto.getLongitude());
  }

  @Transactional
  public void deleteTrade(User user, Long tradeId) {
    BookTrade bookTrade = getBookTrade(tradeId);
    if (user.isAdmin() || user.equals(bookTrade.getUser())) {
      bookTrade.setDeleteYn(Status.Y);
      return;
    }
    throw new AccessDeniedException(Message.FORBIDDEN_BOOK_TRADES_UPDATE);
  }

  @Transactional
  public void changeTrade(User user, Long tradeId, Status tradeYn) {
    BookTrade bookTrade = getBookTrade(tradeId);
    if (!user.equals(bookTrade.getUser())) {
      throw new AccessDeniedException(Message.FORBIDDEN_BOOK_TRADES_UPDATE);
    }
    bookTrade.setTradeYn(tradeYn);
  }
}
