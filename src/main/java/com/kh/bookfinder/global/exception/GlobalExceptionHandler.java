package com.kh.bookfinder.global.exception;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.dto.ErrorResponseBody;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(Message.BAD_REQUEST)
        .details(extractDetailsForField(ex))
        .build();

    return ResponseEntity.badRequest()
        .body(errorResponseBody);
  }

  @ExceptionHandler(InvalidFieldException.class)
  public ResponseEntity<ErrorResponseBody> handleInvalidField(InvalidFieldException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(Message.BAD_REQUEST)
        .details(extractDetailsForField(e))
        .build();
    return ResponseEntity
        .badRequest()
        .body(errorResponseBody);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseBody> handleResourceNotFound(ResourceNotFoundException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(Message.NOT_FOUND)
        .detail(e.getLocalizedMessage())
        .build();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponseBody);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponseBody> handleBadCredentials(BadCredentialsException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(Message.UNAUTHORIZED)
        .detail(e.getLocalizedMessage())
        .build();

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(errorResponseBody);
  }

  private Map<String, String> extractDetailsForField(InvalidFieldException e) {
    Map<String, String> details = new HashMap<>();
    details.put(e.getField(), e.getLocalizedMessage());
    return details;
  }

  private Map<String, String> extractDetailsForField(MethodArgumentNotValidException e) {
    Map<String, String> details = new HashMap<>();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      details.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
    }
    return details;
  }
}
