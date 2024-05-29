package com.kh.bookfinder.user.signup;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.CONFLICT;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.global.exception.DuplicateResourceException;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.user.dto.SendingEmailAuthDto;
import com.kh.bookfinder.user.entity.EmailAuth;
import com.kh.bookfinder.user.repository.UserRepository;
import com.kh.bookfinder.user.service.EmailAuthService;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SendAuthEmailApiTest {

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

  private SendingEmailAuthDto getBaseSendingEmailAuthDto() {
    return SendingEmailAuthDto
        .builder()
        .email("jinho4744@naver.com")
        .build();
  }

  private EmailAuth getMockEmailAuth() {
    return EmailAuth.builder()
        .authId(1L)
        .authCode("validAuth")
        .email("jinho4744@naver.com")
        .build();
  }

  private ResultActions callApiWith(String requestBody) throws Exception {
    return mockMvc.perform(
        post("/api/v1/signup/email")
            .content(requestBody)
            .contentType(MediaType.APPLICATION_JSON)
    );
  }

  @Test
  @DisplayName("유효한 형식의 email이 주어지는 경우")
  public void success_OnValidSendingEmailAuthDto() throws Exception {
    // Given: 유효한 SendingEmailAuthDto가 주어진다.
    SendingEmailAuthDto validEmailAuthDto = getBaseSendingEmailAuthDto();
    String requestBody = objectMapper.writeValueAsString(validEmailAuthDto);
    // And: EamilAuthService가 mockEmailAuth를 반환하도록 mocking한다.
    EmailAuth mockEmailAuth = getMockEmailAuth();
    when(emailAuthService.sendAuthCodeTo(any())).thenReturn(mockEmailAuth);
    // And: EamilAuthService가 MOCK_SIGNING_TOKEN를 반환하도록 mocking한다.
    when(emailAuthService.generateSigningToken(mockEmailAuth)).thenReturn(MOCK_SIGNING_TOKEN);

    // When: Sending Auth Code Email API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 signingToken을 반환한다.
    resultActions.andExpect(jsonPath("$.signingToken", is(MOCK_SIGNING_TOKEN)));
  }

  @Test
  @DisplayName("유효하지 않은 형식의 email이 주어지는 경우")
  public void fail_OnInvaliSendingEmailAuthDto_WithEmail() throws Exception {
    // Given: 유효하지 않은 SendingEmailAuthDto가 주어진다.
    SendingEmailAuthDto invalidEmailAuthDto = getBaseSendingEmailAuthDto();
    invalidEmailAuthDto.setEmail("jinho4744navercom");
    String requestBody = objectMapper.writeValueAsString(invalidEmailAuthDto);

    // When: Sending Auth Code Email API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("이미 DB에 저장된 Email인 경우")
  public void fail_OnInvaliSendingEmailAuthDto_WithAlreadyExistInDB_ForEmail() throws Exception {
    // Given: 유효한 SendingEmailAuthDto가 주어진다.
    SendingEmailAuthDto validEmailAuthDto = getBaseSendingEmailAuthDto();
    String requestBody = objectMapper.writeValueAsString(validEmailAuthDto);
    // And: EmailAuthService가 Exception이 발생하도록 Mocking한다.
    when(userRepository.findByEmail(validEmailAuthDto.getEmail())).thenReturn(Optional.of(MockUser.getMockUser()));
    when(emailAuthService.sendAuthCodeTo(validEmailAuthDto.getEmail()))
        .thenThrow(new DuplicateResourceException(Message.DUPLICATE_EMAIL));

    // When: Sending Auth Code Email API를 호출한다.
    ResultActions resultActions = callApiWith(requestBody);

    // Then: Status는 409 Conflict이다.
    resultActions.andExpect(status().isConflict());
    // And: Response Body로 message와 details가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(CONFLICT.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.DUPLICATE_EMAIL)));
  }

  @Test
  @DisplayName("Header에 유효한 Auhtorization이 포함된 경우")
  public void fail_OnValidAuthorization_InHeader() throws Exception {
    // Given: 유효한 SendingEmailAuthDto가 주어진다.
    SendingEmailAuthDto validEmailAuthDto = getBaseSendingEmailAuthDto();
    String requestBody = objectMapper.writeValueAsString(validEmailAuthDto);
    // And: JwtService가 true를 반환하도록 Mocking한다.
    when(jwtService.validateToken(any())).thenReturn(true);
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(MockUser.getMockUser()));

    // When: Header에 유효한 Authorization을 담아 Sending Auth Code Email API를 호출한다.
    ResultActions resultActions = mockMvc.perform(post("/api/v1/signup/email")
        .content(requestBody)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "validToken")
    );

    // Then: Status는 403 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail가 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.ALREADY_LOGIN)));
  }
}
