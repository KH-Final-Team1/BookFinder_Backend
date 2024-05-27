package com.kh.bookfinder.book_trade.helper;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Status;
import com.kh.bookfinder.book_trade.entity.TradeType;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.helper.MockUser;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

public class MockBookTrade {

  public static BookTrade getMockBookTrade() {
    return getMockBookTradeList(1).get(0);
  }

  public static ArrayList<BookTrade> getMockBookTradeList(int count) {
    ArrayList<BookTrade> result = new ArrayList<>();
    User mockUser = MockUser.getMockUser();
    Book book = Book.builder()
        .isbn(1234567890123L)
        .name("test book name")
        .authors("test book authors")
        .publisher("test book publisher")
        .publicationYear(2024)
        .description("test book description")
        .imageUrl("test book image url")
        .build();
    Borough borough = Borough
        .builder()
        .id(5L)
        .name("관악구")
        .build();
    for (int i = 0; i < count; i++) {
      BookTrade bookTrade = BookTrade
          .builder()
          .id((i + 1L))
          .tradeType(TradeType.BORROW)
          .tradeYn(Status.Y)
          .deleteYn(Status.N)
          .rentalCost(10000)
          .content("test Content")
          .latitude(BigDecimal.valueOf(12.3))
          .longitude(BigDecimal.valueOf(23.4))
          .limitedDate(Date.valueOf("2024-05-21"))
          .createDate(Date.valueOf(LocalDate.now()))
          .updateDate(Date.valueOf(LocalDate.now()))
          .user(mockUser)
          .book(book)
          .borough(borough)
          .build();
      result.add(bookTrade);
    }
    return result;
  }

  public static ArrayList<BookTrade> getMockBookTradeListOnUser(User user, int count) {
    ArrayList<BookTrade> result = new ArrayList<>();
    Book book = Book.builder()
        .isbn(1234567890123L)
        .name("test book name")
        .authors("test book authors")
        .publisher("test book publisher")
        .publicationYear(2024)
        .description("test book description")
        .imageUrl("test book image url")
        .build();
    Borough borough = Borough
        .builder()
        .id(5L)
        .name("관악구")
        .build();
    for (int i = 0; i < count; i++) {
      BookTrade bookTrade = BookTrade
          .builder()
          .id((i + 1L))
          .tradeType(TradeType.BORROW)
          .tradeYn(Status.Y)
          .deleteYn(Status.N)
          .rentalCost(10000)
          .content("test Content")
          .latitude(BigDecimal.valueOf(12.3))
          .longitude(BigDecimal.valueOf(23.4))
          .limitedDate(Date.valueOf("2024-05-21"))
          .createDate(Date.valueOf(LocalDate.now()))
          .updateDate(Date.valueOf(LocalDate.now()))
          .user(user)
          .book(book)
          .borough(borough)
          .build();
      result.add(bookTrade);
    }
    return result;
  }
}
