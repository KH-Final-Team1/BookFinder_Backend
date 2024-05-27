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

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);

    String url = request.getRequestURI();
    if (url.startsWith(SIGNUP_URLS)) {
      ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
          .message(FORBIDDEN)
          .detail(Message.ALREADY_LOGIN)
          .build();
      new ObjectMapper().writeValue(response.getWriter(), errorResponseBody);
    } else {
      response.getWriter().write(accessDeniedException.getLocalizedMessage());
    }
  }
}
