package com.kh.bookfinder.auth.oauth2.controller;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.auth.oauth2.dto.OAuth2SignUpDto;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/oauth2")
public class OAuth2Controller {

  private final UserService userService;

  @PostMapping(value = "/signup", produces = "application/json;charset=UTF-8")
  public ResponseEntity<Map<String, String>> oAuthSignUp(@RequestBody @Valid OAuth2SignUpDto signUpDto) {
    SecurityUserDetails securityUserDetails =
        (SecurityUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    userService.updateSocialGuestToUser(securityUserDetails, signUpDto);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(Map.of("message", Message.SIGNUP_SUCCESS));
  }

}
