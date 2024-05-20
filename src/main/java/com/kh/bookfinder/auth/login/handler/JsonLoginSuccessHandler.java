package com.kh.bookfinder.auth.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.dto.AccessTokenDto;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
public class JsonLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtService jwtService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
    AccessTokenDto accessToken = AccessTokenDto.builder()
        .accessToken(jwtService.createAccessToken(authentication))
        .build();

    new ObjectMapper().writeValue(response.getWriter(), accessToken);
  }
}
