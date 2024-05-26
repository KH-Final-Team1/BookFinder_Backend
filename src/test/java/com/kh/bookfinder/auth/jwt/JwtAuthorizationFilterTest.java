package com.kh.bookfinder.auth.jwt;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
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
public class JwtAuthorizationFilterTest {

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private JwtService jwtService;
  @MockBean
  private UserRepository userRepository;

  @Test
  @DisplayName("Anonymous 권한 경로에 권한 없이 접근 하는 경우")
  public void success_OnAnonymousAuthorization_WithNotAuthorization() throws Exception {
    // When: anonymous 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/anonymous"));

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Anonymous 권한 경로에 권한을 가지고 접근하는 경우")
  public void fail_OnAnonymousAuthorization_WithAuthorization() throws Exception {
    // Given: ADMIN 권한을 가진 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: JwtService가 mockUser의 email을 반환하도록 Mocking한다.
    when(jwtService.extractEmail(any())).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(mockUser.getEmail()))
        .thenReturn(Optional.of(mockUser));

    // When: anonymous 권한 경로로 Token을 담아 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(
        get("/test/v1/anonymous")
            .header("Authorization", "mockToken"));

    // Then: Status는 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Authenticated 권한 경로에 권한 없이 접근하는 경우")
  public void fail_OnAuthenticatedAuthorization_WithNotAuthorization() throws Exception {
    // Given: JwtService가 Exception을 발생하도록 Mocking한다.
    when(jwtService.validateToken(any())).thenThrow(new IllegalArgumentException(Message.NOT_LOGIN));

    // When: authenticated 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/authenticate"));

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.UNAUTHORIZED)));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }

  @Test
  @DisplayName("Authenticated 권한 경로에 권한을 가지고 접근하는 경우")
  public void success_OnAuthenticatedAuthorization_WithAuthorization() throws Exception {
    // Given: USER 권한을 가진 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: JwtService가 mockUser의 email을 반환하도록 Mocking한다.
    when(jwtService.extractEmail(any())).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(mockUser.getEmail()))
        .thenReturn(Optional.of(mockUser));

    // When: authenticated 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/authenticate"));

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("ADMIN 권한 경로에 ADMIN 권한을 가지고 접근하는 경우")
  public void success_OnAdminAuthorization_WithAdminAuthorization() throws Exception {
    // Given: ADMIN 권한을 가진 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: JwtService가 mockUser의 email을 반환하도록 Mocking한다.
    when(jwtService.extractEmail(any())).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(mockUser.getEmail()))
        .thenReturn(Optional.of(mockUser));

    // When: Admin 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/admin"));

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("ADMIN 권한 경로에 USER 권한을 가지고 접근하는 경우")
  public void fail_OnAdminAuthorization_WithUserAuthorization() throws Exception {
    // Given: USER 권한을 가진 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: JwtService가 mockUser의 email을 반환하도록 Mocking한다.
    when(jwtService.extractEmail(any())).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(mockUser.getEmail()))
        .thenReturn(Optional.of(mockUser));

    // When: Admin 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/admin"));

    // Then: Status는 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("ADMIN 권한 경로에 권한 없이 접근하는 경우")
  public void fail_OnAdminAuthorization_WithNotAuthorization() throws Exception {
    // When: Admin 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/admin"));

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("USER 권한 경로에 USER 권한을 가지고 접근하는 경우")
  public void success_OnUserAuthorization_WithUserAuthorization() throws Exception {
    // Given: USER 권한을 가진 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_USER);
    // And: JwtService가 mockUser의 email을 반환하도록 Mocking한다.
    when(jwtService.extractEmail(any())).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(mockUser.getEmail()))
        .thenReturn(Optional.of(mockUser));

    // When: User 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/user"));

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("USER 권한 경로에 ADMIN 권한을 가지고 접근하는 경우")
  public void fail_OnUserAuthorization_WithAdminAuthorization() throws Exception {
    // Given: Admin 권한을 가진 User가 주어진다.
    User mockUser = MockUser.getMockUser();
    mockUser.setRole(UserRole.ROLE_ADMIN);
    // And: JwtService가 mockUser의 email을 반환하도록 Mocking한다.
    when(jwtService.extractEmail(any())).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(mockUser.getEmail()))
        .thenReturn(Optional.of(mockUser));

    // When: User 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/user"));

    // Then: Status는 Not Found이다.
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("User 권한 경로에 권한 없이 접근하는 경우")
  public void fail_OnUserAuthorization_WithNotAuthorization() throws Exception {
    // When: User 권한 경로로 요청을 보낸다.
    ResultActions resultActions = mockMvc.perform(get("/test/v1/user"));

    // Then: Status는 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
  }
}
