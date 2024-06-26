package com.kh.bookfinder.auth.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.global.constants.HttpErrorMessage;
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
    if (isContentTypeException(exception)) {
      sendUnsupportedMediaType(response, exception);
    } else if (isDtoException(exception)) {
      sendBadRequest(response, exception);
    } else if (existsAuthorization(exception)) {
      sendForbidden(response, exception);
    } else {
      sendUnauthorized(response);
    }
  }

  private void sendUnsupportedMediaType(HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
    ErrorResponseBody responseBody = ErrorResponseBody
        .builder()
        .message(HttpErrorMessage.UNSUPPORTED_MEDIA_TYPE)
        .build();
    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }

  private void sendForbidden(HttpServletResponse response, AuthenticationException exception) throws IOException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    ErrorResponseBody responseBody = ErrorResponseBody
        .builder()
        .message(HttpErrorMessage.FORBIDDEN)
        .detail(exception.getLocalizedMessage())
        .build();
    new ObjectMapper().writeValue(response.getWriter(), responseBody);
  }

  private void sendUnauthorized(HttpServletResponse response) throws IOException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    ErrorResponseBody responseBody = ErrorResponseBody
        .builder()
        .message(HttpErrorMessage.UNAUTHORIZED)
        .detail(Message.FAIL_LOGIN)
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
        .message(HttpErrorMessage.BAD_REQUEST)
        .details(objectMapper.readValue(exception.getLocalizedMessage(), Map.class))
        .build();
    objectMapper.writeValue(response.getWriter(), responseBody);
  }

  private boolean isContentTypeException(AuthenticationException exception) {
    return exception.getMessage().contains(HttpErrorMessage.UNSUPPORTED_MEDIA_TYPE.getMessage());
  }

  private boolean isDtoException(AuthenticationException exception) {
    String message = exception.getLocalizedMessage();
    return message.contains(DTO_EMAIL) || message.contains(DTO_PASSWORD);
  }

  private boolean existsAuthorization(AuthenticationException exception) {
    return exception.getMessage().contains(Message.INVALID_LOGIN_WITH_AUTHORIZATION);
  }
}
