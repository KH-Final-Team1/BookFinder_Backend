package com.kh.bookfinder.auth.oauth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.dto.ErrorResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ErrorResponseBody responseBody = ErrorResponseBody
        .builder()
        .message(Message.UNAUTHORIZED)
        .detail(Message.FAIL_LOGIN)
        .build();
    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }
}
