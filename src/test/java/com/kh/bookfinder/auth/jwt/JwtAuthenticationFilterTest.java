package com.kh.bookfinder.auth.jwt;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class JwtAuthenticationFilterTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private UserRepository userRepository;

  @Test
  @DisplayName("Header의 Authorization이 유효한 경우")
  public void success_OnAuthenticatedAuthorization_WithValidAuthorization() throws Exception {
    // Given: 유효한 토큰이 주어진다.
    String validToken = "validToken";
    // And: User가 주어진다.
    User mockUser = MockUser.getMockUser();
    // And: JwtService가 validToken을 반환하도록 Mocking한다.
    when(jwtService.extractAccessToken(any())).thenReturn(validToken);
    // And: JwtService가 true를 반환하도록 Mocking한다.
    when(jwtService.validateToken(validToken)).thenReturn(true);
    // And: JwtService가 mockUser의 email을 반환하도록 Mocking한다.
    when(jwtService.extractEmail(validToken)).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

    // When: authenticated 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/authenticate")
        .header("Authorization", validToken));

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Jwt의 서명이 잘못된 경우")
  public void fail_OnAuthenticatedAuthorization_WithInvalidJwtSignature() throws Exception {
    // Given: 유효하지 않은 토큰이 주어진다.
    String invalidToken = "invalidToken";
    // And: JwtService가 invalidToken을 반환하도록 Mocking한다.
    when(jwtService.extractAccessToken(any())).thenReturn(invalidToken);
    // And: JwtService가 Exception을 발생하도록 Mocking한다.
    when(jwtService.validateToken(invalidToken)).thenThrow(new JwtException(Message.INVALID_JWT_SIGNATURE));

    // When: authenticated 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/authenticate")
        .header("Authorization", invalidToken));

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.INVALID_JWT_SIGNATURE)));
  }

  @Test
  @DisplayName("Jwt가 유효하지 않은 경우")
  public void fail_OnAuthenticatedAuthorization_WithInvalidJwt() throws Exception {
    // Given: 유효하지 않은 토큰이 주어진다.
    String invalidToken = "invalidToken";
    // And: JwtService가 invalidToken을 반환하도록 Mocking한다.
    when(jwtService.extractAccessToken(any())).thenReturn(invalidToken);
    // And: JwtService가 Exception을 발생하도록 Mocking한다.
    when(jwtService.validateToken(invalidToken)).thenThrow(new JwtException(Message.INVALID_ACCESS_TOKEN));

    // When: authenticated 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/authenticate")
        .header("Authorization", invalidToken));

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.INVALID_ACCESS_TOKEN)));
  }

  @Test
  @DisplayName("Jwt가 만료된 경우")
  public void fail_OnAuthenticatedAuthorization_WithExpiredToken() throws Exception {
    // Given: 유효하지 않은 토큰이 주어진다.
    String invalidToken = "invalidToken";
    // And: JwtService가 invalidToken을 반환하도록 Mocking한다.
    when(jwtService.extractAccessToken(any())).thenReturn(invalidToken);
    // And: JwtService가 Exception을 발생하도록 Mocking한다.
    when(jwtService.validateToken(invalidToken)).thenThrow(new JwtException(Message.EXPIRED_ACCESS_TOKEN));

    // When: authenticated 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/authenticate")
        .header("Authorization", invalidToken));

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.EXPIRED_ACCESS_TOKEN)));
  }

  @Test
  @DisplayName("Jwt에서 추출한 email이 DB에 없는 경우")
  public void fail_OnAuthenticatedAuthorization_WithExtractedEmailNotExistInDB() throws Exception {
    // Given: 유효한 토큰이 주어진다.
    String validToken = "validToken";
    // And: JwtService가 validToken을 반환하도록 Mocking한다.
    when(jwtService.extractAccessToken(any())).thenReturn(validToken);
    // And: JwtService가 true를 반환하도록 Mocking한다.
    when(jwtService.validateToken(validToken)).thenReturn(true);

    // When: authenticated 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/authenticate")
        .header("Authorization", validToken));

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_USER)));
  }
}
