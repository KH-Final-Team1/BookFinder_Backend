package com.kh.bookfinder.auth.login.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.login.dto.LoginDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class JsonLoginFilter extends AbstractAuthenticationProcessingFilter {

  private static final String LOGIN_REQUEST_URL = "/api/v1/login";
  private static final String LOGIN_REQUEST_HTTP_METHOD = "POST";
  private static final String CONTENT_TYPE = "application/json";
  private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
      new AntPathRequestMatcher(LOGIN_REQUEST_URL, LOGIN_REQUEST_HTTP_METHOD);

  private final ObjectMapper objectMapper;
  private final Validator validator;

  public JsonLoginFilter(ObjectMapper objectMapper, Validator validator) {
    super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
    this.objectMapper = objectMapper;
    this.validator = validator;
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
      throws AuthenticationException, IOException {
    if (request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
      throw new AuthenticationServiceException("유효하지 않은 Content-Type 입니다.");
    }
    LoginDto loginDto = objectMapper.readValue(request.getInputStream(), LoginDto.class);
    Set<ConstraintViolation<LoginDto>> violations = validator.validate(loginDto);
    if (!violations.isEmpty()) {
      Map<String, String> errorMap = violations
          .stream()
          .collect(Collectors.toMap(k -> k.getPropertyPath().toString(), ConstraintViolation::getMessage));
      throw new BadCredentialsException(objectMapper.writeValueAsString(errorMap));
    }
    return getAuthentication(loginDto);
  }

  private Authentication getAuthentication(LoginDto loginDto) {
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
    return this.getAuthenticationManager().authenticate(authenticationToken);
  }


}
