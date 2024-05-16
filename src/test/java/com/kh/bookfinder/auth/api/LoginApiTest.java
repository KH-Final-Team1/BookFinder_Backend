package com.kh.bookfinder.auth.api;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.login.dto.LoginDto;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LoginApiTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private UserRepository userRepository;

  private LoginDto getBaseLoginDto() {
    return LoginDto
        .builder()
        .email("jinho4744@naver.com")
        .password("test_password_123")
        .build();
  }

  private ResultActions callLoginApi(LoginDto invalidLoginDto) throws Exception {
    return mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/login")
        .content(objectMapper.writeValueAsString(invalidLoginDto))
        .contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @DisplayName("DB에 저장된 email과 password가 일치하는 경우")
  public void loginSuccessOnValidLoginDtoTest() throws Exception {
    // Given: 로그인 정보가 주어진다.
    LoginDto loginDto = getBaseLoginDto();
    // And: Repository가 Mock User객체를 리턴하도록 모킹한다.
    User mockUser = User.builder()
        .id(1L)
        .email("jinho4744@naver.com")
        .password(new BCryptPasswordEncoder().encode("test_password_123"))
        .nickname("고소하게")
        .role(UserRole.ROLE_ADMIN)
        .build();
    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(mockUser));

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(loginDto);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    // And: Response Body로 AccessToken이 반환된다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").exists());
  }

  @Test
  @DisplayName("Login Dto의 email 형식이 유효하지 않은 경우")
  public void loginFailOnInvalidEmailFormatTest() throws Exception {
    // Given: 유효하지 않은 이메일 형식이 주어진다.
    LoginDto invalidLoginDto = getBaseLoginDto();
    invalidLoginDto.setEmail("jinho4744navercom");

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(invalidLoginDto);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("Login Dto의 password 형식이 유효하지 않은 경우")
  public void loginFailOnInvalidPasswordFormatTest() throws Exception {
    // Given: 유효하지 않은 비밀번호 형식이 주어진다.
    LoginDto invalidLoginDto = getBaseLoginDto();
    invalidLoginDto.setPassword("zz");

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(invalidLoginDto);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.details.password", is(Message.INVALID_PASSWORD)));
  }

  @Test
  @DisplayName("DB에 email에 해당하는 튜플이 없는 경우")
  public void loginFailOnNotExistTupleForEmailTest() throws Exception {
    // Given: 로그인 정보가 주어진다.
    LoginDto loginDto = getBaseLoginDto();
    // And: DB에 email에 해당하는 튜플이 없다.

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(loginDto);

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.UNAUTHORIZED)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.FAIL_LOGIN)));
  }

  @Test
  @DisplayName("DB에 password가 일치하지 않는 경우")
  public void loginFailOnNotEqualsPasswordTest() throws Exception {
    // Given: password가 DB의 튜플과 일치하지 않는 로그인 정보가 주어진다.
    LoginDto loginDto = getBaseLoginDto();
    loginDto.setPassword("test_password_1");
    // And: Repository가 Mock User객체를 리턴하도록 모킹한다.
    User mockUser = User.builder()
        .id(1L)
        .email("jinho4744@naver.com")
        .password(new BCryptPasswordEncoder().encode("test_password_123"))
        .nickname("고소하게")
        .role(UserRole.ROLE_ADMIN)
        .build();
    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(mockUser));

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(loginDto);

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isUnauthorized());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.UNAUTHORIZED)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.FAIL_LOGIN)));
  }
}
