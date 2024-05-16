package com.kh.bookfinder.auth.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.dto.ErrorResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class JsonLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

  private static final String DTO_EMAIL = "email";
  private static final String DTO_PASSWORD = "password";

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    if (isDtoException(exception)) {
      sendBadRequest(response, exception);
    } else {
      sendUnauthorized(response, exception);
    }
  }

  private void sendUnauthorized(HttpServletResponse response, AuthenticationException exception) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ErrorResponseBody responseBody = ErrorResponseBody
        .builder()
        .message(Message.UNAUTHORIZED)
        .detail(exception.getLocalizedMessage())
        .build();
    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }


  @SuppressWarnings("unchecked")
  private void sendBadRequest(HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    ErrorResponseBody responseBody = ErrorResponseBody
        .builder()
        .message(Message.BAD_REQUEST)
        .details(objectMapper.readValue(exception.getLocalizedMessage(), Map.class))
        .build();
    objectMapper.writeValue(response.getWriter(), responseBody);
  }

  private boolean isDtoException(AuthenticationException exception) {
    String message = exception.getLocalizedMessage();
    return message.contains(DTO_EMAIL) || message.contains(DTO_PASSWORD);
  }
}
