package com.kh.bookfinder.global.exception;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.CONFLICT;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;

import com.kh.bookfinder.global.dto.ErrorResponseBody;
import java.util.HashMap;
import java.util.Map;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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
        .message(BAD_REQUEST)
        .details(extractDetailsForField(ex))
        .build();

    return ResponseEntity.badRequest()
        .body(errorResponseBody);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(BAD_REQUEST)
        .detail(ex.getLocalizedMessage())
        .build();

    return ResponseEntity.badRequest()
        .body(errorResponseBody);
  }

  @ExceptionHandler(InvalidFieldException.class)
  public ResponseEntity<ErrorResponseBody> handleInvalidField(InvalidFieldException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(BAD_REQUEST)
        .details(extractDetailsForField(e))
        .build();
    return ResponseEntity
        .badRequest()
        .body(errorResponseBody);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponseBody> handleResourceNotFound(ResourceNotFoundException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(NOT_FOUND)
        .detail(e.getLocalizedMessage())
        .build();
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(errorResponseBody);
  }

  @ExceptionHandler(DuplicateResourceException.class)
  public ResponseEntity<ErrorResponseBody> handleDuplicateResource(DuplicateResourceException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(CONFLICT)
        .detail(e.getLocalizedMessage())
        .build();

    return ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(errorResponseBody);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponseBody> handleBadCredentials(BadCredentialsException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(UNAUTHORIZED)
        .detail(e.getLocalizedMessage())
        .build();

    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(errorResponseBody);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ErrorResponseBody> handleUnauthorizedException(UnauthorizedException e) {
    Map<String, String> details = new HashMap<>();
    details.put("권한 오류", e.getLocalizedMessage());
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(UNAUTHORIZED)
        .details(details)
        .build();

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
        .body(errorResponseBody);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponseBody> handleAccessDenied(AccessDeniedException e) {
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(FORBIDDEN)
        .detail(e.getLocalizedMessage())
        .build();

    return ResponseEntity
        .status(HttpStatus.FORBIDDEN)
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
