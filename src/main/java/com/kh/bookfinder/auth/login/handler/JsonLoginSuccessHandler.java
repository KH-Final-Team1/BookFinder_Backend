package com.kh.bookfinder.auth.login.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.dto.AccessTokenDto;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@RequiredArgsConstructor
public class JsonLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtService jwtService;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType("application/json;charset=UTF-8");
    String accessToken = buildAccessTokenFromAuth(authentication);

    new ObjectMapper().writeValue(response.getWriter(),
        AccessTokenDto.builder().accessToken(accessToken).build());
  }

  private String buildAccessTokenFromAuth(Authentication authentication) {
    SecurityUserDetails principal = (SecurityUserDetails) authentication.getPrincipal();
    String email = principal.getUsername();
    String authorities = principal.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return jwtService.createAccessToken(email, authorities);
  }
}
