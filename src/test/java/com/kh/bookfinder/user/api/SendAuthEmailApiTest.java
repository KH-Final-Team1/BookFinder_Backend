package com.kh.bookfinder.user.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.dto.SendingEmailAuthDto;
import com.kh.bookfinder.user.entity.EmailAuth;
import com.kh.bookfinder.user.helper.MockUser;
import com.kh.bookfinder.user.repository.UserRepository;
import com.kh.bookfinder.user.service.EmailAuthService;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SendAuthEmailApiTest {

  /* TODO: 이미 가입된 이메일로 요청을 보낼 경우에 대한 테스트가 없음
   */
  private static final String MOCK_SIGNING_TOKEN = "eyJlbWFpbCI6ImppbmhvNDc0NEBuYXZlci5jb20iLCJjb2RlIjoiZHdmOHlGclgifQ==";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private EmailAuthService emailAuthService;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;
  private EmailAuth mockEmailAuth;

  @BeforeEach
  public void setup() {
    mockEmailAuth = mock(EmailAuth.class);
  }

  @Test
  @DisplayName("유효한 형식의 email이 주어지는 경우")
  public void sendAuthEmailSuccessTest() throws Exception {
    // Given: 유효한 형식의 SendingEmailAuthDto가 주어진다.
    SendingEmailAuthDto requestDto = SendingEmailAuthDto
        .builder()
        .email("jinho4744@naver.com")
        .build();
    String requestBody = objectMapper.writeValueAsString(requestDto);
    // And: EmailService의 sendAuthCodeTo()를 모킹한다.
    when(emailAuthService.sendAuthCodeTo(any())).thenReturn(mockEmailAuth);
    when(mockEmailAuth.generateSigningToken()).thenReturn(MOCK_SIGNING_TOKEN);

    // When: Sending Auth Code Email API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/email")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 200 Ok이다.
        .andExpect(MockMvcResultMatchers.status().isOk())
        // And: Response Body로 signingToken을 반환한다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.signingToken", is(MOCK_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("유효하지 않은 형식의 email이 주어지는 경우")
  public void sendAuthEmailFailTest() throws Exception {
    // Given: 유효하지 않은 형식의 SendingEmailAuthDto가 주어진다.
    SendingEmailAuthDto requestDto = SendingEmailAuthDto
        .builder()
        .email("jinho4744navercom")
        .build();
    String requestBody = objectMapper.writeValueAsString(requestDto);

    // When: Sending Auth Code Email API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/email")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
    )
        // Then: Status는 400 Bad Request이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        // And: Response Body로 message와 details가 반환된다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("Header에 유효한 Auhtorization이 포함된 경우")
  public void sendAuthEmailFailOnAuthorizationInHeaderTest() throws Exception {
    // Given: 유효한 SendingEmailAuthDto가 주어진다.
    SendingEmailAuthDto requestDto = SendingEmailAuthDto
        .builder()
        .email("jinho6442@naver.com")
        .build();
    String requestBody = objectMapper.writeValueAsString(requestDto);
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(MockUser.getMockUser()));
    // And: JwtService를 Mocking한다.
    when(jwtService.validateToken(any())).thenReturn(true);

    // When: Header에 유효한 Authorization을 담아 Sending Auth Code Email API를 호출한다.
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/v1/signup/email")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "validToken")
    )
        // Then: Status는 403 Forbidden이다.
        .andExpect(MockMvcResultMatchers.status().isForbidden())
        // And: Response Body로 message와 detail가 반환된다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(FORBIDDEN.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.ALREADY_LOGIN)));
  }
}
