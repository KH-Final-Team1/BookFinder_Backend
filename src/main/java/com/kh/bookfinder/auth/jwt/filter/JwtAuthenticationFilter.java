package com.kh.bookfinder.auth.jwt.filter;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
  private final UserRepository userRepository;
  private final AntPathMatcher antPathMatcher = new AntPathMatcher();

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    // 로그인은 아묻따 JsonLoginFilter에서 처리
    if (request.getRequestURI().equals(LOGIN_URL)) {
      return true;
    }

    if (request.getHeader("Authorization") != null) {
      return false;
    }
    return EXCEPT_FILTER_URLS.stream().anyMatch(pattern -> antPathMatcher.match(pattern, request.getRequestURI()));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String accessToken = jwtService.extractAccessToken(request);
      jwtService.validateToken(accessToken);

      String email = jwtService.extractEmail(accessToken);
      User serviceUser = userRepository.findByEmail(email).orElseThrow(
          () -> new ResourceNotFoundException(Message.NOT_FOUND_USER)
      );

      SecurityUserDetails principal = new SecurityUserDetails(serviceUser);
      setAuthentication(principal);
    } catch (Exception e) {
      request.setAttribute("exception", e);
    }
    filterChain.doFilter(request, response);
  }

  private void setAuthentication(UserDetails principal) {
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities())
    );
  }
}
