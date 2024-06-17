package com.kh.bookfinder.auth.jwt.handler;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.dto.ErrorResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtForbiddenHandler implements AccessDeniedHandler {

  private static final String SIGNUP_URLS = "/api/v1/signup";
  private static final String BOOKS_URLS = "/api/v1/books";
  private static final String PATCH = "PATCH";

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    ErrorResponseBody errorResponseBody = buildErrorResponseBody(accessDeniedException, request);

    new ObjectMapper().writeValue(response.getWriter(), errorResponseBody);
  }

  private ErrorResponseBody buildErrorResponseBody(AccessDeniedException accessDeniedException,
      HttpServletRequest request) {
    String url = request.getRequestURI();
    String method = request.getMethod();
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(FORBIDDEN)
        .build();

    if (url.startsWith(SIGNUP_URLS)) {
      errorResponseBody.setDetail(Message.ALREADY_LOGIN);
    } else if (url.startsWith(BOOKS_URLS) && method.equals(PATCH)) {
      errorResponseBody.setDetail(Message.FORBIDDEN_BOOK_STATUS_UPDATE);
    } else {
      errorResponseBody.setDetail(accessDeniedException.getLocalizedMessage());
    }

    return errorResponseBody;
  }
}
