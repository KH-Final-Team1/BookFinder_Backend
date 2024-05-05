package com.kh.bookfinder.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.constants.Message;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class SignUpDuplicateCheckApiTest {

  /* TODO: 데이터베이스를 모킹해야할까? 아니면 실제 테스트이니 모킹할 필요 없나?
           중복된 이메일인 경우, Bad Request가 나을까? 아니면 Conflict(409)가 나을까?
   */

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

  @Test
  @DisplayName("email 형식이 유효하지 않은 경우")
  public void emailDuplicateCheckFailTest1() throws Exception {
    // Given: field=email, value=jinhokhkr인 DuplicateCheckDto가 주어진다
    DuplicateCheckDto invalidDuplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("email")
        .value("jinhokhkr")
        .build();

    // When: Duplicate Check API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/signup/duplicate")
            .param("field", invalidDuplicateCheckDto.getField())
            .param("value", invalidDuplicateCheckDto.getValue())
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
        // And: details는 {"email": "유효하지 않은 이메일 형식입니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("field가 email이 아닌 경우")
  public void emailDuplicateCheckFailTest2() throws Exception {
    // Given: field=invalid, value=jinho@kh.kr인 DuplicateCheckDto가 주어진다
    DuplicateCheckDto invalidDuplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("invalid")
        .value("jinho@kh.kr")
        .build();

    // When: Duplicate Check API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/signup/duplicate")
            .param("field", invalidDuplicateCheckDto.getField())
            .param("value", invalidDuplicateCheckDto.getValue())
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
        // And: details는 {"field": "field는 email만 가능합니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.field", is(Message.INVALID_FIELD)));
  }

  @Test
  @DisplayName("이미 가입한 email인 경우")
  @Sql("classpath:oneUserInsert.sql")
  public void emailDuplicateCheckFailTest3() throws Exception {
    // Given: email이 jinho@kh.kr인 User를 데이터베이스에 추가한다.
    assertThat(this.userRepository.findByEmail("jinho@kh.kr").orElse(null)).isNotNull();
    // And: field=email, value=jinho@kh.kr인 DuplicateCheckDto가 주어진다
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
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
        // And: details는 {"email": "이미 가입된 이메일입니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.DUPLICATE_EMAIL)));
  }
}
