package com.kh.bookfinder.book_trade.controller;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.book_trade.dto.BookTradeDetailResponseDto;
import com.kh.bookfinder.book_trade.dto.BookTradeListResponseDto;
import com.kh.bookfinder.book_trade.dto.BookTradeRequestDto;
import com.kh.bookfinder.book_trade.dto.BookTradeYnDto;
import com.kh.bookfinder.book_trade.entity.BookTrade;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.InvalidFieldException;
import com.kh.bookfinder.user.entity.User;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

  @GetMapping(value = "list/{boroughId}", produces = "application/json;charset=UTF-8")
  public ResponseEntity<List<BookTradeListResponseDto>> getBookTrades(
      @PathVariable(name = "boroughId") Long boroughId) {
    if (!Borough.isValid(boroughId)) {
      throw new InvalidFieldException("boroughId", Message.INVALID_BOROUGH);
    }
    SecurityUserDetails principal = (SecurityUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User serviceUser = principal.getServiceUser();

    List<BookTrade> bookTradeList = bookTradeService.getBookTradesByBoroughId(serviceUser, boroughId);

    List<BookTradeListResponseDto> response = bookTradeList
        .stream()
        .map(x -> x.toResponse(BookTradeListResponseDto.class))
        .collect(Collectors.toList());

    return ResponseEntity.ok().body(response);
  }

  @GetMapping("/{tradeId}")
  public ResponseEntity<BookTradeDetailResponseDto> getBookTrade(@PathVariable(name = "tradeId") Long tradeId) {
    SecurityUserDetails principal = (SecurityUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User serviceUser = principal.getServiceUser();

    BookTrade bookTrade = bookTradeService.getBookTrade(serviceUser, tradeId);
    return ResponseEntity.ok().body(bookTrade.toResponse(BookTradeDetailResponseDto.class));
  }

  @PostMapping
  public ResponseEntity<BookTrade> createBookTrade(@RequestBody @Valid BookTradeRequestDto tradeDto) {
    SecurityUserDetails principal = (SecurityUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User serviceUser = principal.getServiceUser();

    bookTradeService.saveBookTrade(serviceUser, tradeDto);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/{tradeId}")
  public ResponseEntity<Map<String, String>> updateBookTrade(@PathVariable(name = "tradeId") Long tradeId,
      @RequestBody @Valid BookTradeRequestDto tradeDto) {
    SecurityUserDetails principal = (SecurityUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User serviceUser = principal.getServiceUser();

    bookTradeService.updateBookTrade(serviceUser, tradeId, tradeDto);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_UPDATE));
  }

  @DeleteMapping("/{tradeId}")
  public ResponseEntity<Map<String, String>> deleteBookTrade(@PathVariable(name = "tradeId") Long tradeId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    SecurityUserDetails principal = (SecurityUserDetails) authentication.getPrincipal();
    String email = principal.getUsername();
    bookTradeService.deleteTrade(email, tradeId);
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_DELETE));
  }

  @PatchMapping("/{tradeId}")
  public ResponseEntity<Map<String, String>> changeTradeStatus(@PathVariable(name = "tradeId") Long tradeId,
      @RequestBody @Valid BookTradeYnDto tradeYn) {
    SecurityUserDetails principal = (SecurityUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User serviceUser = principal.getServiceUser();

    bookTradeService.changeTrade(serviceUser, tradeId, tradeYn.getTradeYn());
    return ResponseEntity.ok().body(Map.of("message", Message.SUCCESS_CHANGE));
  }
}