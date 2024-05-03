package com.kh.bookfinder.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.constants.Message;
import com.kh.bookfinder.dto.SignUpDto;
import com.kh.bookfinder.entity.User;
import com.kh.bookfinder.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class UserControllerTest {

  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("유효한 SignUpDto가 주어지는 경우")
  public void signUpSuccessTest() throws Exception {
    // Given: 유효한 SignUpDto가 주어진다.
    SignUpDto validSignUpDto = this.buildValidSignUpDto();
    String requestBody = objectMapper.writeValueAsString(validSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 201 Created 이다.
        // And: message는 "가입이 완료되었습니다.\n환영합니다.\n로그인 후 이용해주세요."이다.
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.SIGNUP_SUCCESS)));
    // And: 데이터베이스에 해당 User 정보가 저장된다.
    User actual = userRepository.findByEmail(validSignUpDto.getEmail()).orElse(null);
    assertThat(actual).isNotNull();
  }

  private SignUpDto buildValidSignUpDto() {
    return SignUpDto.builder()
        .email("jinho@kh.kr")
        .password("q1w2e3r41!")
        .passwordConfirm("q1w2e3r41!")
        .nickname("nickname")
        .address("address")
        .phone("01012345678")
        .build();
  }
}
