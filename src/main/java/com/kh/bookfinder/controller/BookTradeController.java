package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.BookTradeDTO;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.entity.TradeType;
import com.kh.bookfinder.service.BookService;
import com.kh.bookfinder.service.BookTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

    BookTrade bookTrade = BookTrade.builder()
        .book(book)
        .tradeType(tradeType)
        .rentalCost(tradeDTO.getRentalCost())
        .limitedDate(tradeDTO.getLimitedDate())
        .content(tradeDTO.getContent())
        .latitude(tradeDTO.getLatitude())
        .longitude(tradeDTO.getLongitude())
        .build();

    bookTradeService.createBookTrade(bookTrade);
    return ResponseEntity.ok().body(bookTrade);
  }

}