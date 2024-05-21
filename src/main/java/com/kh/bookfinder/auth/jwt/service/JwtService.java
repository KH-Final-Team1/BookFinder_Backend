package com.kh.bookfinder.auth.jwt.service;

import com.kh.bookfinder.auth.login.dto.SecurityUserDetails;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String CLAIM_EMAIL = "email";
  public static final String CLAIM_AUTHORITIES = "auth";
  private static final String BEARER = "Bearer ";
  @Value("${jwt.secretKey}")
  private String secretKey;
  @Value("${jwt.access.expiration}")
  private Long accessTokenExpiration;
  private final UserRepository userRepository;
  @Value("${jwt.access.header}")
  private String accessHeader;

  public String createAccessToken(Authentication authentication) {
    SecurityUserDetails user = (SecurityUserDetails) authentication.getPrincipal();
    String email = user.getUsername();
    String authorities = user.getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    return Jwts.builder()
        .subject(ACCESS_TOKEN_SUBJECT)
        .claim(CLAIM_EMAIL, email)
        .claim(CLAIM_AUTHORITIES, authorities)
        .expiration(new Date(new Date().getTime() + accessTokenExpiration))
        .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .compact();
  }

  public String extractAccessToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(accessHeader);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
      return bearerToken.replace(BEARER, "");
    }
    return null;
  }

  public SecurityUserDetails getSecurityUserDetails(String accessToken) {
    Claims claims = Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .build()
        .parseSignedClaims(accessToken)
        .getPayload();
    User serviceUser = userRepository
        .findByEmail(claims.get(CLAIM_EMAIL, String.class))
        .orElseThrow(() -> new JwtException(Message.INVALID_ACCESS_TOKEN));
    return new SecurityUserDetails(serviceUser);
  }

  public boolean validateToken(String token)
      throws ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException {
    try {
      Jwts.parser()
          .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (SecurityException | MalformedJwtException e) {
      throw new JwtException(Message.INVALID_JWT_SIGNATURE);
    } catch (UnsupportedJwtException e) {
      throw new JwtException(Message.INVALID_ACCESS_TOKEN);
    } catch (ExpiredJwtException e) {
      throw new JwtException(Message.EXPIRED_ACCESS_TOKEN);
    } catch (IllegalArgumentException e) {
      throw new JwtException(Message.NOT_LOGIN);
    }
  }
}
