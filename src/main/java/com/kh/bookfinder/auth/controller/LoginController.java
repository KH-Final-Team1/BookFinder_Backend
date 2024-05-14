package com.kh.bookfinder.auth.controller;

import com.kh.bookfinder.auth.dto.LoginDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> login(@RequestBody @Valid LoginDto loginDto) {
    authenticationManagerBuilder.getObject().authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
    );
    return ResponseEntity.ok("login!");
  }
}
