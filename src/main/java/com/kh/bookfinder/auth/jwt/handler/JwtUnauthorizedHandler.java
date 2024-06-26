package com.kh.bookfinder.auth.jwt.handler;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.dto.ErrorResponseBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtUnauthorizedHandler implements AuthenticationEntryPoint {


  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException {
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    Exception jwtException = (Exception) request.getAttribute("exception");
    ErrorResponseBody errorResponseBody = ErrorResponseBody.builder()
        .message(UNAUTHORIZED)
        .build();

    if (jwtException == null) {
      errorResponseBody.setDetail(Message.NOT_LOGIN);
    } else {
      errorResponseBody.setDetail(jwtException.getLocalizedMessage());
    }

    new ObjectMapper().writeValue(response.getWriter(), errorResponseBody);
  }
}
