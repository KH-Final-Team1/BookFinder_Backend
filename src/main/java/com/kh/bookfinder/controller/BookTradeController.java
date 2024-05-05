package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.BookTradeDTO;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.service.BookService;
import com.kh.bookfinder.service.BookTradeService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

  @GetMapping("/{boroughId}")
  public ResponseEntity<ArrayList<BookTrade>> getBookTrades(@PathVariable(name = "boroughId") Long boroughId) {
    if (boroughId < 1 || boroughId > 25) throw new IllegalArgumentException(Message.INVALID_BOROUGH);

    ArrayList<BookTrade> bookTradeList = bookTradeService.getBookTrades(boroughId);

    return ResponseEntity.ok().body(bookTradeList);
  }

  @PostMapping
  public ResponseEntity<BookTrade> createBookTrade(@RequestBody @Validated BookTradeDTO tradeDTO) {
    Long isbn = Long.valueOf(tradeDTO.getIsbn());

    Book book = bookService.findBook(isbn)
        .orElseThrow(() -> new IllegalArgumentException(Message.INVALID_ISBN));

    BookTrade bookTrade = BookTrade.builder()
        .book(book)
        .tradeType(tradeDTO.getTradeType())
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