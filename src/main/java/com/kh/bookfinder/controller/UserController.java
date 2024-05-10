package com.kh.bookfinder.controller;

import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.CheckingVerificationDto;
import com.kh.bookfinder.dto.DuplicateCheckDto;
import com.kh.bookfinder.dto.SendingEmailAuthDto;
import com.kh.bookfinder.dto.SignUpDto;
import com.kh.bookfinder.entity.EmailAuth;
import com.kh.bookfinder.service.EmailAuthService;
import com.kh.bookfinder.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signup")
public class UserController {

  private final UserService userService;
  private final EmailAuthService emailAuthService;

  @PostMapping(value = "", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> signUp(@RequestBody @Valid SignUpDto signUpDto)
      throws JSONException {
    userService.save(signUpDto);
    JSONObject responseBody = new JSONObject();
    responseBody.put("message", Message.SIGNUP_SUCCESS);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(responseBody.toString());
  }

  @GetMapping(value = "/duplicate", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> checkDuplicate(@Valid DuplicateCheckDto duplicateCheckDto)
      throws JSONException {
    userService.checkDuplicate(duplicateCheckDto);
    JSONObject responseBody = new JSONObject();

    responseBody.put("message", Message.getSuccessMessageBy(duplicateCheckDto.getField()));
    return ResponseEntity
        .ok()
        .body(responseBody.toString());
  }

  @PostMapping(value = "/email", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> sendAuthEmail(@Valid @RequestBody SendingEmailAuthDto requestBody)
      throws JSONException {
    EmailAuth emailAuth = this.emailAuthService.sendAuthCodeTo(requestBody.getEmail());
    String signingToken = emailAuth.generateSigningToken();

    JSONObject responseBody = new JSONObject();
    responseBody.put("signingToken", signingToken);

    return ResponseEntity
        .ok()
        .body(responseBody.toString());
  }

  @PostMapping(value = "/verification-code", produces = "application/json;charset=UTF-8")
  public ResponseEntity<String> checkVerification(@Valid @RequestBody CheckingVerificationDto requestBody)
      throws JSONException {
    String singingToken = this.emailAuthService.checkVerification(requestBody);
    JSONObject responseBody = new JSONObject();
    responseBody.put("signingToken", singingToken);

    return ResponseEntity
        .ok()
        .body(responseBody.toString());
  }
}
