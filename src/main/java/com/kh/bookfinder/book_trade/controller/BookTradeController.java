package com.kh.bookfinder.book_trade.controller;

import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.service.BookService;
import com.kh.bookfinder.book_trade.dto.BookTradeDetailResponseDto;
import com.kh.bookfinder.book_trade.dto.BookTradeListResponseDto;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.entity.Borough;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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

  @GetMapping(value = "list/{boroughId}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<List<BookTradeListResponseDto>> getBookTrades(
      @PathVariable(name = "boroughId") Long boroughId) {
    if (!Borough.isValid(boroughId)) {
      throw new InvalidFieldException("boroughId", Message.INVALID_BOROUGH);
    }
    ArrayList<BookTrade> bookTradeList = bookTradeService.getBookTrades(boroughId);

    List<BookTradeListResponseDto> response = bookTradeList.stream().map(
        x -> x.toResponse(BookTradeListResponseDto.class)
    ).collect(Collectors.toList());

    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/{tradeId}")
  public ResponseEntity<BookTradeDetailResponseDto> getBookTrade(@PathVariable(name = "tradeId") Long tradeId) {
    BookTrade bookTrade = bookTradeService.getBookTrade(tradeId);
    return ResponseEntity.ok().body(bookTrade.toResponse(BookTradeDetailResponseDto.class));
  }

  @PostMapping
  public ResponseEntity<BookTrade> createBookTrade(@RequestBody @Valid BookTradeRequestDto tradeDto) {
    Book book = bookService.findApprovedBook(tradeDto.getIsbn());
    BookTrade bookTrade = tradeDto.toEntity(book);

    bookTradeService.saveBookTrade(bookTrade);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/{tradeId}")
  public ResponseEntity<Map<String, String>> updateBookTrade(@PathVariable(name = "tradeId") Long tradeId,
      @RequestBody @Valid BookTradeRequestDto tradeDto) {
    BookTrade bookTrade = bookTradeService.findTrade(tradeId);
    Book book = bookService.findApprovedBook(tradeDto.getIsbn());

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