package com.kh.bookfinder.user.signup;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.user.dto.CheckingVerificationDto;
import com.kh.bookfinder.user.entity.EmailAuth;
import com.kh.bookfinder.user.repository.EmailAuthRepository;
import com.kh.bookfinder.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class CheckVerificationAuthCodeApiTest {

  // Email로 보낸 Auth 코드 확인하는 API
  /*
  {
   "email": "jinho4744@naver.com",
   "code": "dwf8yFrX"
  }
   */
  private static final String MOCK_SIGNING_TOKEN = "eyJlbWFpbCI6ImppbmhvNDc0NEBuYXZlci5jb20iLCJjb2RlIjoiZHdmOHlGclgifQ==";
  private static final String MOCK_AUTH_CODE = "dwf8yFrX";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  private EmailAuth mockEmailAuth;
  @MockBean
  private EmailAuthRepository emailAuthRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;

  @BeforeEach
  public void setup() {
    mockEmailAuth = EmailAuth.builder()
        .email("jinho4744@naver.com")
        .authCode(MOCK_AUTH_CODE)
        .build();
  }

  private ResultActions callApiWith(String requestBody) throws Exception {
    return mockMvc.perform(
        post("/api/v1/signup/verification-code")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
    );
  }

  private CheckingVerificationDto getBaseCheckingVerificationDto() {
    return CheckingVerificationDto
        .builder()
        .authCode(MOCK_AUTH_CODE)
        .signingToken(MOCK_SIGNING_TOKEN)
        .build();
  }

  @Test
  @DisplayName("유효한 authCode와 signingToken이 주어진 경우")
  public void success_OnValidAuthCodeAndSigningToken() throws Exception {
    // Given: 유효한 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto verificationDto = getBaseCheckingVerificationDto();
    String requestBody = objectMapper.writeValueAsString(verificationDto);
    // And: EmailAuthRepository가 mockEmailAuth를 반환하도록 mocking한다.
    when(emailAuthRepository.findByEmailAndAuthCode(anyString(), anyString())).thenReturn(Optional.of(mockEmailAuth));

    // When: Check Verification Auth Code API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 signingToken을 반환한다.
    resultActions.andExpect(jsonPath("$.signingToken", is(MOCK_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("유효하지 않은 authCode가 주어진 경우")
  public void fail_OnInvalidCheckingVerificationDto_WithAuthCode() throws Exception {
    // Given: 유효하지 않은 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto invalidCheckingVerificationDto = getBaseCheckingVerificationDto();
    invalidCheckingVerificationDto.setAuthCode("invalidAuthCode");
    String requestBody = objectMapper.writeValueAsString(invalidCheckingVerificationDto);

    // When: Check Verification Auth Code API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details가 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.authCode", is(Message.INVALID_AUTH_CODE)));
  }

  @Test
  @DisplayName("유효하지 않은 signingToken이 주어진 경우")
  public void fail_OnInvalidCheckingVerificationDto_WithSigningToken() throws Exception {
    // Given: 유효하지 않은 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto invalidCheckingVerificationDto = getBaseCheckingVerificationDto();
    invalidCheckingVerificationDto.setSigningToken("invalidSigningToken");
    String requestBody = objectMapper.writeValueAsString(invalidCheckingVerificationDto);

    // When: Check Verification Auth Code API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details가 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.signingToken", is(Message.INVALID_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("signingToken의 email과 authCode가 db에 없는 경우")
  public void fail_OnNotExistInDB_ForEmailAuth() throws Exception {
    // Given: CheckingVerificationDto가 주어진다.
    CheckingVerificationDto checkingVerificationDto = getBaseCheckingVerificationDto();
    String requestBody = objectMapper.writeValueAsString(checkingVerificationDto);

    // When: Check Verification Auth Code API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 404 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail가 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_EMAIL_AUTH)));
  }

  @Test
  @DisplayName("authCode의 expiration이 지난 경우")
  public void fail_OnExpirationAuthCode() throws Exception {
    // Given: 유효한 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto validCheckingVerificationDto = getBaseCheckingVerificationDto();
    String requestBody = objectMapper.writeValueAsString(validCheckingVerificationDto);
    // And: EmailAuthRepository가 expiredMockEmailAuth를 반환하도록 mocking 한다.
    EmailAuth invalidMockEmailAuth = mockEmailAuth;
    invalidMockEmailAuth.setExpiration(LocalDateTime.now().minusMinutes(1));
    when(emailAuthRepository.findByEmailAndAuthCode(anyString(), anyString()))
        .thenReturn(Optional.of(invalidMockEmailAuth));

    // When: Check Verification Auth Code API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details가 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.authCode", is(Message.EXPIRED_AUTH_CODE)));
  }

  @Test
  @DisplayName("Header에 유효한 Authorization이 포함된 경우")
  public void fail_OnValidAuthorization_InHeader() throws Exception {
    // Given: 유효한 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto requestDto = getBaseCheckingVerificationDto();
    String requestBody = objectMapper.writeValueAsString(requestDto);
    // And: EmailAuthRepository가 mockEmailAuth를 반환하도록 mocking한다.
    when(emailAuthRepository.findByEmailAndAuthCode(anyString(), anyString())).thenReturn(Optional.of(mockEmailAuth));
    // And: JwtService가 true를 반환하도록 mocking한다.
    when(jwtService.validateToken(any())).thenReturn(true);
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(MockUser.getMockUser()));

    // When: Header에 유효한 Authorization을 담아 SignUp API를 호출한다.
    ResultActions resultActions = mockMvc.perform(
        post("/api/v1/signup/verification-code")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "validAccessToken")
    );

    // Then: Status는 403 Forbidden 이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.ALREADY_LOGIN)));
  }
}
