package com.kh.bookfinder.auth.jwt.filter;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String LOGIN_URL = "/api/v1/login";
  private static final List<String> EXCEPT_FILTER_URLS =
      List.of("/api/v1/signup/**", "/api/v1/books/list", "/api/v1/books/{isbn}");
  private final JwtService jwtService;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getRequestURI().equals(LOGIN_URL)
        || (request.getHeader("Authorization") == null
        && EXCEPT_FILTER_URLS.stream().anyMatch(pattern -> antPathMatcher.match(pattern, request.getRequestURI())));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String accessToken = jwtService.extractAccessToken(request);
      jwtService.validateToken(accessToken);
      SecurityUserDetails principal = jwtService.getSecurityUserDetails(accessToken);
      setAuthentication(principal, accessToken);
    } catch (Exception e) {
      request.setAttribute("exception", e);
    }
    filterChain.doFilter(request, response);
  }

  private void setAuthentication(UserDetails principal, String accessToken) {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, accessToken, principal.getAuthorities())
    );
  }
}
