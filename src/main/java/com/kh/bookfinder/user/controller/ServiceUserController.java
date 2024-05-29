package com.kh.bookfinder.user.controller;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.book_trade.dto.BookTradeListResponseDto;
import com.kh.bookfinder.book_trade.service.BookTradeService;
import com.kh.bookfinder.user.dto.MyInfoResponseDto;
import com.kh.bookfinder.user.entity.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ServiceUserController {

  private final BookTradeService bookTradeService;

  @GetMapping(value = "/my-info", produces = "application/json;charset=UTF-8")
  public ResponseEntity<MyInfoResponseDto> getMyInfo() {
    SecurityUserDetails principal = (SecurityUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    User serviceUser = principal.getServiceUser();

    List<BookTradeListResponseDto> bookTrades = bookTradeService.getBookTradesByUserId(serviceUser.getId())
        .stream()
        .map(x -> x.toResponse(BookTradeListResponseDto.class))
        .collect(Collectors.toList());

    MyInfoResponseDto responseDto = serviceUser.toMyInfoResponse(bookTrades);

    return ResponseEntity.ok(responseDto);
  }
}
