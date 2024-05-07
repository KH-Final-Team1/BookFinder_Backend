package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Borough;
import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.BookTradeDTO;
import com.kh.bookfinder.entity.Book;
import com.kh.bookfinder.entity.BookTrade;
import com.kh.bookfinder.exception.InvalidFieldException;
import com.kh.bookfinder.service.BookService;
import com.kh.bookfinder.service.BookTradeService;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
@RequestMapping("/api/v1/trades")
public class BookTradeController {

  @Autowired
  BookTradeService bookTradeService;
  @Autowired
  BookService bookService;

  @GetMapping("list/{boroughId}")
  public ResponseEntity<ArrayList<BookTrade>> getBookTrades(@PathVariable(name = "boroughId") Long boroughId) {
    if (boroughId < Borough.MIN_BOROUGH || boroughId > Borough.MAX_BOROUGH) {
      throw new InvalidFieldException("지역 번호 오류", Message.INVALID_BOROUGH);
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
  public ResponseEntity<BookTrade> createBookTrade(@RequestBody @Valid BookTradeDTO tradeDTO) {
    Book book = bookService.findBook(tradeDTO.getIsbn())
        .orElseThrow(() -> new InvalidFieldException("ISBN", Message.INVALID_ISBN));

    BookTrade bookTrade = BookTrade.builder()
        .book(book)
        .tradeType(tradeDTO.getTradeType())
        .rentalCost(tradeDTO.getRentalCost())
        .limitedDate(tradeDTO.getLimitedDate())
        .content(tradeDTO.getContent())
        .latitude(tradeDTO.getLatitude())
        .longitude(tradeDTO.getLongitude())
        .build();

    bookTradeService.saveBookTrade(bookTrade);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/{tradeId}")
  public ResponseEntity<Map<String, String>> updateBookTrade(@PathVariable(name = "tradeId") Long tradeId,
                                                @RequestBody @Valid BookTradeDTO tradeDTO) {
    BookTrade bookTrade = bookTradeService.findTrade(tradeId)
        .orElseThrow(() -> new ResourceNotFoundException(Message.INVALID_TRADE));

    Book book = bookService.findBook(tradeDTO.getIsbn())
        .orElseThrow(() -> new InvalidFieldException("ISBN", Message.INVALID_ISBN));

    bookTrade = BookTrade.builder()
        .id(bookTrade.getId())
        .book(book)
        .tradeType(tradeDTO.getTradeType())
        .rentalCost(tradeDTO.getRentalCost())
        .limitedDate(tradeDTO.getLimitedDate())
        .content(tradeDTO.getContent())
        .latitude(tradeDTO.getLatitude())
        .longitude(tradeDTO.getLongitude())
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