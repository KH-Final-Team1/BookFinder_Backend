package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Borough;
import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.BookTradeDto;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.exception.InvalidFieldException;
import com.kh.bookfinder.service.BookService;
import com.kh.bookfinder.service.BookTradeService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trades")
public class BookTradeController {

  private final BookTradeService bookTradeService;
  private final BookService bookService;

  @GetMapping("list/{boroughId}")
  public ResponseEntity<ArrayList<BookTrade>> getBookTrades(@PathVariable(name = "boroughId") Long boroughId) {
    if (boroughId < Borough.MIN_BOROUGH || boroughId > Borough.MAX_BOROUGH) {
      throw new InvalidFieldException("borough Id", Message.INVALID_BOROUGH);
    }
    ArrayList<BookTrade> bookTradeList = bookTradeService.getBookTrades(boroughId);
    return ResponseEntity.ok().body(bookTradeList);
  }

  @GetMapping("/{tradeId}")
  public ResponseEntity<BookTrade> getBookTrade(@PathVariable(name = "tradeId") Long tradeId) {
    BookTrade bookTrade = bookTradeService.getBookTrade(tradeId);
    return ResponseEntity.ok().body(bookTrade);
  }

  @PostMapping
  public ResponseEntity<BookTrade> createBookTrade(@RequestBody @Valid BookTradeDto tradeDto) {
    Book book = bookService.findBook(tradeDto.getIsbn());

    BookTrade bookTrade = BookTrade.builder()
        .book(book)
        .tradeType(tradeDto.getTradeType())
        .rentalCost(tradeDto.getRentalCost())
        .limitedDate(tradeDto.getLimitedDate())
        .content(tradeDto.getContent())
        .latitude(tradeDto.getLatitude())
        .longitude(tradeDto.getLongitude())
        .build();

    bookTradeService.saveBookTrade(bookTrade);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/{tradeId}")
  public ResponseEntity<Map<String, String>> updateBookTrade(@PathVariable(name = "tradeId") Long tradeId,
      @RequestBody @Valid BookTradeDto tradeDto) {
    BookTrade bookTrade = bookTradeService.findTrade(tradeId);
    Book book = bookService.findBook(tradeDto.getIsbn());

    bookTrade = BookTrade.builder()
        .id(bookTrade.getId())
        .book(book)
        .tradeType(tradeDto.getTradeType())
        .rentalCost(tradeDto.getRentalCost())
        .limitedDate(tradeDto.getLimitedDate())
        .content(tradeDto.getContent())
        .latitude(tradeDto.getLatitude())
        .longitude(tradeDto.getLongitude())
        .createDate(bookTrade.getCreateDate())
        .build();

    bookTradeService.saveBookTrade(bookTrade);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_UPDATE));
  }

  @DeleteMapping("/{tradeId}")
  public ResponseEntity<Map<String, String>> deleteBookTrade(@PathVariable(name = "tradeId") Long tradeId) {
    bookTradeService.deleteTrade(tradeId);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_DELETE));
  }

}