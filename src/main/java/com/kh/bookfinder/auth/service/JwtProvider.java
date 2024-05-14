package com.kh.bookfinder.auth.service;

import com.kh.bookfinder.auth.dto.SecurityUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

  public static final String CLAIM_AUTHORITIES = "auth";
  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String CLAIM_EMAIL = "email";
  @Value("${jwt.secretKey}")
  private String secretKey;
  @Value("${jwt.access.expiration}")
  private Long accessTokenExpiration;

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

}
