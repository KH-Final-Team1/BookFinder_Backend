package com.kh.bookfinder.user.controller;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.user.dto.MyInfoResponseDto;
import com.kh.bookfinder.user.service.UserService;
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

  private final UserService userService;

  @GetMapping(value = "/my-info", produces = "application/json;charset=UTF-8")
  public ResponseEntity<MyInfoResponseDto> getMyInfo() {
    SecurityUserDetails principal = (SecurityUserDetails)
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String email = principal.getUsername();

    MyInfoResponseDto response = userService.getUserWithBookTrades(email);

    return ResponseEntity.ok(response);
  }
}
