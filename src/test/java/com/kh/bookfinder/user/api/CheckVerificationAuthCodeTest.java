package com.kh.bookfinder.user.api;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.dto.CheckingVerificationDto;
import com.kh.bookfinder.user.entity.EmailAuth;
import com.kh.bookfinder.user.repository.EmailAuthRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class CheckVerificationAuthCodeTest {

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

  @BeforeEach
  public void setup() {
    mockEmailAuth = EmailAuth.builder()
        .email("jinho4744@naver.com")
        .authCode(MOCK_AUTH_CODE)
        .build();
  }

  @Test
  @DisplayName("유효한 authCode와 signingToken이 주어진 경우")
  public void checkVerificationAuthCodeSuccessTest() throws Exception {
    // Given: 유효한 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto requestDto = CheckingVerificationDto
        .builder()
        .authCode(MOCK_AUTH_CODE)
        .signingToken(MOCK_SIGNING_TOKEN)
        .build();
    String requestBody = objectMapper.writeValueAsString(requestDto);
    // And: EmailAuthRepository를 mocking 한다.
    when(emailAuthRepository.findByEmailAndAuthCode(anyString(), anyString()))
        .thenReturn(Optional.of(mockEmailAuth));

    // When: Check Verification Auth Code API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/verification-code")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 200 Ok이다.
        .andExpect(MockMvcResultMatchers.status().isOk())
        // And: Response Body로 signingToken을 반환한다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.signingToken", is(MOCK_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("유효하지 않은 authCode가 주어진 경우")
  public void checkVerificationAuthCodeFailOnInvalidAuthCodeTest() throws Exception {
    // Given: 유효하지 않은 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto invalidCheckingVerificationDto = CheckingVerificationDto
        .builder()
        .authCode("invalidAuthCode")
        .signingToken(MOCK_SIGNING_TOKEN)
        .build();
    String requestBody = objectMapper.writeValueAsString(invalidCheckingVerificationDto);

    // When: Check Verification Auth Code API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/verification-code")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 400 Bad Request이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // And: Response Body로 message와 details가 반환한다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.authCode", is(Message.INVALID_AUTH_CODE)));
  }

  @Test
  @DisplayName("유효하지 않은 signingToken이 주어진 경우")
  public void checkVerificationAuthCodeFailOnInvalidSigningTokenTest() throws Exception {
    // Given: 유효하지 않은 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto invalidCheckingVerificationDto = CheckingVerificationDto
        .builder()
        .authCode(MOCK_AUTH_CODE)
        .signingToken("InvalidSigningToken")
        .build();
    String requestBody = objectMapper.writeValueAsString(invalidCheckingVerificationDto);

    // When: Check Verification Auth Code API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/verification-code")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 400 Bad Request이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // And: Response Body로 message와 details가 반환한다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.signingToken", is(Message.INVALID_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("authCode와 signingToken의 authCode가 일치하지 않는 경우")
  public void checkVerificationAuthCodeFailOnNotEqualAuthCodeTest() throws Exception {
    // Given: authCode와 SigningToken의 authCode가 일치하지 않은 CheckingVerificationDto가 주어진다.
    CheckingVerificationDto invalidCheckingVerificationDto = CheckingVerificationDto
        .builder()
        .authCode("Abcd1eFg")
        .signingToken(MOCK_SIGNING_TOKEN)
        .build();
    String requestBody = objectMapper.writeValueAsString(invalidCheckingVerificationDto);

    // When: Check Verification Auth Code API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/verification-code")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 400 Bad Request이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // And: Response Body로 message와 details가 반환한다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.signingToken", is(Message.INVALID_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("signingToken의 email과 authCode가 db에 없는 경우")
  public void checkVerificationAuthCodeFailOnNotExistInDbTest() throws Exception {
    // Given: CheckingVerificationDto가 주어진다.
    CheckingVerificationDto invalidCheckingVerificationDto = CheckingVerificationDto
        .builder()
        .authCode(MOCK_AUTH_CODE)
        .signingToken(MOCK_SIGNING_TOKEN)
        .build();
    String requestBody = objectMapper.writeValueAsString(invalidCheckingVerificationDto);

    // When: Check Verification Auth Code API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/verification-code")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 400 Bad Request이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // And: Response Body로 message와 details가 반환한다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.signingToken", is(Message.INVALID_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("authCode의 expiration이 지난 경우")
  public void checkVerificationAuthCodeFailOnExpirationAuthCodeTest() throws Exception {
    // Given: CheckingVerificationDto가 주어진다.
    CheckingVerificationDto invalidCheckingVerificationDto = CheckingVerificationDto
        .builder()
        .authCode(MOCK_AUTH_CODE)
        .signingToken(MOCK_SIGNING_TOKEN)
        .build();
    String requestBody = objectMapper.writeValueAsString(invalidCheckingVerificationDto);
    // EmailAuthRepository를 mocking 한다.
    mockEmailAuth.setExpiration(LocalDateTime.now().minusMinutes(1));
    when(emailAuthRepository.findByEmailAndAuthCode(anyString(), anyString()))
        .thenReturn(Optional.of(mockEmailAuth));

    // When: Check Verification Auth Code API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/verification-code")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 400 Bad Request이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // And: Response Body로 message와 details가 반환한다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.authCode", is(Message.EXPIRED_AUTH_CODE)));
  }
}
