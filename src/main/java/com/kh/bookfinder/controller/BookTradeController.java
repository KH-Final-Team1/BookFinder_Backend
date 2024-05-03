package com.kh.bookfinder.controller;

import com.kh.bookfinder.dto.BookTradeDTO;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.entity.TradeType;
import com.kh.bookfinder.service.BookService;
import com.kh.bookfinder.service.BookTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trades")
public class BookTradeController {

  @Autowired
  BookTradeService bookTradeService;
  @Autowired
  BookService bookService;

  @PostMapping
  public ResponseEntity<BookTrade> createBookTrade(@RequestBody BookTradeDTO tradeDTO) {
    Long isbn = Long.valueOf(tradeDTO.getIsbn());

    Book book = bookService.findBook(isbn)
        .orElseThrow(() -> new IllegalArgumentException("Book not found for ISBN: " + tradeDTO.getIsbn()));

    TradeType tradeType = TradeType.fromString(tradeDTO.getTradeType());

    BookTrade bookTrade = new BookTrade();

    bookTrade.setBook(book);
    bookTrade.setTradeType(tradeType);
    bookTrade.setAmount(tradeDTO.getAmount());
    bookTrade.setLimitedDate(tradeDTO.getLimitedDate());
    bookTrade.setContent(tradeDTO.getContent());
    bookTrade.setLatitude(tradeDTO.getLatitude());
    bookTrade.setLongitude(tradeDTO.getLongitude());

    bookTradeService.createBookTrade(bookTrade);
    return ResponseEntity.ok().body(bookTrade);
  }

}
