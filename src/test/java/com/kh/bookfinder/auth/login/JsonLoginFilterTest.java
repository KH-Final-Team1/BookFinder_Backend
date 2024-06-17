package com.kh.bookfinder.auth.login;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.login.dto.LoginDto;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.user.entity.User;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class JsonLoginFilterTest {

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

  private ResultActions callLoginApi(LoginDto loginDto) throws Exception {
    return mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/login")
        .content(objectMapper.writeValueAsString(loginDto))
        .contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @DisplayName("DB에 저장된 email과 password가 일치하는 경우")
  public void success_onValidLoginDto() throws Exception {
    // Given: 유효한 LoginDto가 주어진다.
    LoginDto validLoginDto = getBaseLoginDto();

    // Mocking: UserRepository가 mockUser를 리턴
    User mockUser = MockUser.getMockUser();
    when(userRepository.findByEmail(validLoginDto.getEmail())).thenReturn(Optional.of(mockUser));

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(validLoginDto);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 AccessToken이 반환된다.
    resultActions.andExpect(jsonPath("$.accessToken").exists());
  }

  @Test
  @DisplayName("Login Dto의 email 형식이 유효하지 않은 경우")
  public void fail_onInvalidLoginDto_withEmailFormat() throws Exception {
    // Given: 유효하지 않은 LoginDto가 주어진다.
    LoginDto invalidLoginDto = getBaseLoginDto();
    invalidLoginDto.setEmail("jinho4744navercom");

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(invalidLoginDto);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("Login Dto의 password 형식이 유효하지 않은 경우")
  public void fail_onInvalidLoginDto_withPasswordFormat() throws Exception {
    // Given: 유효하지 않은 LoginDto가 주어진다.
    LoginDto invalidLoginDto = getBaseLoginDto();
    invalidLoginDto.setPassword("zz");

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(invalidLoginDto);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.password", is(Message.INVALID_PASSWORD)));
  }

  @Test
  @DisplayName("DB에 email에 해당하는 튜플이 없는 경우")
  public void fail_onNotExistInDB_forEmail() throws Exception {
    // Given: 유효한 LoginDto가 주어진다.
    LoginDto validLoginDto = getBaseLoginDto();

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(validLoginDto);

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.FAIL_LOGIN)));
  }

  @Test
  @DisplayName("password가 DB 튜플의 password와 일치하지 않는 경우")
  public void fail_onNotEqualsPassword_withUserInDB() throws Exception {
    // Given: 유효한 LoginDto가 주어진다.
    LoginDto validLoginDto = getBaseLoginDto();
    validLoginDto.setPassword("test_password_1");
    // And: Repository가 Mock User객체를 리턴하도록 모킹한다.
    User mockUser = MockUser.getMockUser();
    when(userRepository.findByEmail(validLoginDto.getEmail())).thenReturn(Optional.of(mockUser));

    // When: Login API를 호출한다.
    ResultActions resultActions = callLoginApi(validLoginDto);

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.FAIL_LOGIN)));
  }

  @Test
  @DisplayName("Header에 Authorization을 포함한 경우")
  public void fail_onExistsAuthorizationInHeader() throws Exception {
    // Given: 유효한 LoginDto가 주어진다.
    LoginDto validLoginDto = getBaseLoginDto();

    // When: Header에 "Authorization"을 추가하여 Login API를 호출한다.
    ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/login")
        .content(objectMapper.writeValueAsString(validLoginDto))
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "testAccessToken"));

    // Then: Status는 403 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.INVALID_LOGIN_WITH_AUTHORIZATION)));
  }
}
