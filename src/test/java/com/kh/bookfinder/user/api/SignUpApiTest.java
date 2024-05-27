package com.kh.bookfinder.user.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.borough.repository.BoroughRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.dto.SignUpDto;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.transaction.Transactional;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SignUpApiTest {

  // 회원 가입 API
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private BoroughRepository boroughRepository;
  @MockBean
  private JwtService jwtService;

  private Borough getMockBorough() {
    return Borough.builder()
        .id(5L)
        .name("관악구")
        .build();
  }

  private SignUpDto getBaseSingUpDto() {
    return SignUpDto.builder()
        .email("jinho@kh.kr")
        .password("q1w2e3r41!")
        .passwordConfirm("q1w2e3r41!")
        .nickname("nickname")
        .address("서울시 관악구 어딘가")
        .phone("01012345678")
        .build();
  }

  private ResultActions callApiWith(String requestBody) throws Exception {
    return mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
        );
  }

  @Test
  @DisplayName("유효한 SignUpDto가 주어지는 경우")
  public void success_OnValidSignUpDto() throws Exception {
    // Given: 유효한 SignUpDto가 주어진다.
    SignUpDto validSignUpDto = getBaseSingUpDto();
    String requestBody = objectMapper.writeValueAsString(validSignUpDto);
    // And: BoroughRepository가 mockBorough를 반환하도록 Mocking한다.
    when(boroughRepository.findByName(any())).thenReturn(Optional.of(getMockBorough()));

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 201 Created 이다.
    resultActions.andExpect(status().isCreated());
    // And: ResponseBody로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.SIGNUP_SUCCESS)));
  }

  @Test
  @DisplayName("SignUpDto의 email이 유효하지 않은 경우")
  public void fail_OnInvalidSignUpDto_WithEmail() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (email)
    SignUpDto invalidSignUpDto = getBaseSingUpDto();
    invalidSignUpDto.setEmail("jinhokhkr");
    String requestBody = objectMapper.writeValueAsString(invalidSignUpDto);

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("SignUpDto의 email이 이미 가입한 email인 경우")
  public void fail_OnAlreadyExistInDB_WithEmailInSignupDto() throws Exception {
    // Given: 유효한 SignUpDto가 주어진다.
    SignUpDto validSignUpDto = getBaseSingUpDto();
    String requestBody = objectMapper.writeValueAsString(validSignUpDto);
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    User mockUser = MockUser.getMockUser();
    mockUser.setEmail(validSignUpDto.getEmail());
    when(userRepository.findByEmail(validSignUpDto.getEmail()))
        .thenReturn(Optional.of(mockUser));

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.email", is(Message.DUPLICATE_EMAIL)));
  }

  @Test
  @DisplayName("SignUpDto의 password가 유효하지 않은 경우")
  public void fail_OnInvalidSignUpDto_WithPassword() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (password)
    SignUpDto invalidSignUpDto = getBaseSingUpDto();
    invalidSignUpDto.setPassword("1234");
    String requestBody = objectMapper.writeValueAsString(invalidSignUpDto);

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.password", is(Message.INVALID_PASSWORD)));
  }

  @Test
  @DisplayName("SignUpDto의 password와 passwordConfirm이 일치하지 않는 경우")
  public void fail_OnInvalidSignUpDto_WithPasswordConfirm() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (passwordConfirm)
    SignUpDto invalidSignUpDto = getBaseSingUpDto();
    invalidSignUpDto.setPasswordConfirm("q1w2e3r42@");
    String requestBody = objectMapper.writeValueAsString(invalidSignUpDto);

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.password", is(Message.INVALID_PASSWORD_CONFIRM)));
  }

  @Test
  @DisplayName("SignUpDto의 nickname이 유효하지 않은 경우")
  public void fail_OnInvalidSignUpDto_WithNickname() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (nickname)
    SignUpDto invalidSignUpDto = getBaseSingUpDto();
    invalidSignUpDto.setNickname("ㅂㅈㄷㄱ");
    String requestBody = objectMapper.writeValueAsString(invalidSignUpDto);

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.nickname", is(Message.INVALID_NICKNAME)));
  }

  @Test
  @DisplayName("SignUpDto의 nickname이 이미 가입한 nickname인 경우")
  public void fail_OnAlreadyExistInDB_WithNicknameInSignupDto() throws Exception {
    // Given: 유효한 SignUpDto가 주어진다.
    SignUpDto validSignUpDto = getBaseSingUpDto();
    String requestBody = objectMapper.writeValueAsString(validSignUpDto);
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    User mockUser = MockUser.getMockUser();
    mockUser.setNickname(validSignUpDto.getNickname());
    when(userRepository.findByNickname(validSignUpDto.getNickname()))
        .thenReturn(Optional.of(mockUser));

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.nickname", is(Message.DUPLICATE_NICKNAME)));
  }

  @Test
  @DisplayName("SignUpDto의 phone이 유효하지 않은 경우")
  public void fail_OnInvalidSignUpDto_WithPhone() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (phone)
    SignUpDto invalidSignUpDto = getBaseSingUpDto();
    invalidSignUpDto.setPhone("12345678912345");
    String requestBody = objectMapper.writeValueAsString(invalidSignUpDto);

    // When: SignUp API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.phone", is(Message.INVALID_PHONE)));
  }

  @Test
  @DisplayName("Header에 유효한 Authorization이 포함된 경우")
  public void fail_OnValidAuthorization_InHeader() throws Exception {
    // Given: 유효한 SignUpDto가 주어진다.
    SignUpDto validSignUpDto = getBaseSingUpDto();
    String requestBody = objectMapper.writeValueAsString(validSignUpDto);
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(MockUser.getMockUser()));
    // And: JwtService가 true를 반환하도록 Mocking한다.
    when(jwtService.validateToken(any())).thenReturn(true);

    // When: Header에 유효한 Authorization을 담아 SignUp API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
            .header("Authorization", "validToken")
        );

    // Then: Status는 403 Forbidden 이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.ALREADY_LOGIN)));
  }
}
