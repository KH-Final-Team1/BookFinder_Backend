package com.kh.bookfinder.controller;

import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.dto.DuplicateCheckDto;
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
public class SignUpDuplicateCheckApiTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private UserRepository userRepository;

  @Test
  @DisplayName("유효한 DuplicateCheckDto가 주어지는 경우")
  public void emailDuplicateCheckSuccessTest() throws Exception {
    // Given: field=email, value=jinho@kh.kr인 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("email")
        .value("jinho@kh.kr")
        .build();

    // When: Duplicate Check API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/signup/duplicate")
            .param("field", validDuplicateCheckDto.getField())
            .param("value", validDuplicateCheckDto.getValue())
        )
        // Then: Status는 200 Ok이다.
        // And: message는 "가입이 가능한 이메일입니다."이다.
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is("가입이 가능한 이메일입니다.")));
  }

}
