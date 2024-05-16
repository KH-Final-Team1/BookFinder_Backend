package com.kh.bookfinder.auth.login.controller;

import com.kh.bookfinder.auth.jwt.dto.AccessTokenDto;
import com.kh.bookfinder.auth.jwt.service.JwtProvider;
import com.kh.bookfinder.auth.login.dto.LoginDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LoginController {

  private final JwtProvider jwtProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
  public ResponseEntity<AccessTokenDto> login(@RequestBody @Valid LoginDto loginDto) {
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(
        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
    );

    AccessTokenDto responseBody = AccessTokenDto
        .builder()
        .accessToken(jwtProvider.createAccessToken(authentication))
        .build();
    return ResponseEntity.ok(responseBody);
  }
}
