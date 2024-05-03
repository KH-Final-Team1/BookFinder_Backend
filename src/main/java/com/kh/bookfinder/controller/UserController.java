package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SignUpDto;
import com.kh.bookfinder.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(value = "/api/v1/signup", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> signUp(@RequestBody @Valid SignUpDto signUpDto)
      throws JSONException {
    userService.save(signUpDto);
    JSONObject responseBody = new JSONObject();
    responseBody.put("message", Message.SIGNUP_SUCCESS);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(responseBody.toString());
  }

}
