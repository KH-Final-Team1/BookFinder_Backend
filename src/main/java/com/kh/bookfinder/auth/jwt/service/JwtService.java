package com.kh.bookfinder.auth.jwt.service;

import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.repository.UserRepository;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

  private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
  private static final String CLAIM_EMAIL = "email";
  private static final String CLAIM_USER_ID = "id";
  private static final String CLAIM_BOROUGH_ID = "boroughId";
  private static final String CLAIM_BOROUGH_NAME = "boroughName";
  public static final String CLAIM_AUTHORITIES = "auth";
  private static final String BEARER = "Bearer ";
  @Value("${jwt.secretKey}")
  private String secretKey;
  @Value("${jwt.access.expiration}")
  private Long accessTokenExpiration;
  @Value("${jwt.access.header}")
  private String accessHeader;
  private final UserRepository userRepository;

  public String createAccessToken(String email, String authorities) {
    User user = userRepository.findByEmail(email).orElseThrow(
        () -> new ResourceNotFoundException(Message.NOT_FOUND_USER)
    );
    return Jwts.builder()
        .subject(ACCESS_TOKEN_SUBJECT)
        .claim(CLAIM_USER_ID, user.getId())
        .claim(CLAIM_EMAIL, user.getEmail())
        .claim(CLAIM_BOROUGH_ID, user.getBorough().getId())
        .claim(CLAIM_BOROUGH_NAME, user.getBorough().getName())
        .claim(CLAIM_AUTHORITIES, authorities)
        .expiration(new Date(new Date().getTime() + accessTokenExpiration))
        .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .compact();
  }

  public String createAccessToken(String email, String authorities) {
    User user = userRepository.findByEmail(email).orElseThrow(
        () -> new ResourceNotFoundException(Message.NOT_FOUND_USER)
    );
    Borough borough = boroughRepository.findByName(user.getBoroughName()).orElseThrow(
        () -> new ResourceNotFoundException(Message.NOT_FOUND)
    );
    return Jwts.builder()
        .subject(ACCESS_TOKEN_SUBJECT)
        .claim(CLAIM_USER_ID, user.getId())
        .claim(CLAIM_EMAIL, user.getEmail())
        .claim(CLAIM_BOROUGH_ID, borough.getId())
        .claim(CLAIM_BOROUGH_NAME, borough.getName())
        .claim(CLAIM_AUTHORITIES, authorities)
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

  public String extractEmail(String accessToken) {
    return Jwts.parser()
        .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
        .build()
        .parseSignedClaims(accessToken)
        .getPayload()
        .get(CLAIM_EMAIL, String.class);
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
