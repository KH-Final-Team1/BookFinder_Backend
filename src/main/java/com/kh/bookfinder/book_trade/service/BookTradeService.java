package com.kh.bookfinder.book_trade.service;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.service.BookService;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.repository.BookTradeRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.UnauthorizedException;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.service.UserService;
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
  private final UserService userService;
  private final BookService bookService;

  public BookTrade findTrade(Long tradeId) {
    return bookTradeRepository.findById(tradeId)
        .orElseThrow(() -> new ResourceNotFoundException(Message.NOT_FOUND_TRADE));
  }

  public BookTrade getBookTrade(User serviceUser, Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId);
    if (isAdmin(serviceUser)) {
      return bookTrade;
    }
    if (isDeleted(bookTrade)) {
      throw new ResourceNotFoundException(Message.DELETED_TRADE);
    }
    if (!serviceUser.getBorough().equals(bookTrade.getBorough())) {
      throw new AccessDeniedException(Message.FORBIDDEN_BOOK_TRADES_READ);
    }

    return bookTrade;
  }

  public BookTrade getBookTrade(Long tradeId) {
    BookTrade bookTrade = findTrade(tradeId);
    if (isDeleted(bookTrade)) {
      throw new ResourceNotFoundException(Message.DELETED_TRADE);
    }

    return bookTrade;
  }

  public List<BookTrade> getBookTradesByBoroughId(User serviceUser, Long boroughId) {
    if (isAdmin(serviceUser)) {
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
  public void deleteTrade(String email, Long tradeId) {
    User user = userService.findUser(email);
    BookTrade bookTrade = getBookTrade(tradeId);
    if (user.equals(bookTrade.getUser())) {
      bookTrade.setDeleteYn(Status.Y);
    } else {
      throw new UnauthorizedException(Message.NOT_AUTHORIZED);
    }
  }

  @Transactional
  public void changeTrade(String email, Long tradeId, Status tradeYn) {
    User user = userService.findUser(email);
    BookTrade bookTrade = getBookTrade(tradeId);
    if (user.equals(bookTrade.getUser())) {
      bookTrade.setTradeYn(tradeYn);
    } else {
      throw new UnauthorizedException(Message.NOT_AUTHORIZED);
    }
  }

  private boolean isAdmin(User serviceUser) {
    return serviceUser.getRole() == UserRole.ROLE_ADMIN;
  }

  private boolean isDeleted(BookTrade bookTrade) {
    return bookTrade.getDeleteYn().equals(Status.Y);
  }

}
