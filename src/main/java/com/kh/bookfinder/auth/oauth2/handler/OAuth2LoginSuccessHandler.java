package com.kh.bookfinder.auth.oauth2.handler;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.auth.oauth2.dto.CustomOAuth2User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final JwtService jwtService;
  private final UserRepository userRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    CustomOAuth2User customOAuth2User = (CustomOAuth2User) authentication.getPrincipal();

    if (customOAuth2User.getRole() == UserRole.ROLE_SOCIAL_GUEST) {
      String tempAccessToken = jwtService
          .createTempAccessTokenForOAuthSignUp(customOAuth2User.getEmail(), customOAuth2User.getRole().name());
      response.sendRedirect("http://localhost:3000/oauth2/sign-up?token=" + tempAccessToken);
    } else {
      String accessToken = jwtService.createAccessToken(customOAuth2User.getEmail(), customOAuth2User.getRole().name());
      Cookie cookie = new Cookie("accessToken", accessToken);
      cookie.setPath("/");
      cookie.setMaxAge(30);
      response.addCookie(cookie);

      response.sendRedirect("http://localhost:3000/oauth2/login");
    }
  }
}
