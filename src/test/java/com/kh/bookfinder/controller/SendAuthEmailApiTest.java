package com.kh.bookfinder.controller;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SendingEmailAuthDto;
import com.kh.bookfinder.entity.EmailAuth;
import com.kh.bookfinder.service.EmailAuthService;
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
public class SendAuthEmailApiTest {

  private static final String MOCK_SIGNING_TOKEN = "eyJlbWFpbCI6ImppbmhvNDc0NEBuYXZlci5jb20iLCJjb2RlIjoiZHdmOHlGclgifQ==";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private EmailAuthService emailAuthService;
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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }
}
