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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class SignUpApiTest {

  /* TODO: 이메일 인증 API 추가되면 Signup 테스트도 수정되어야 함
           주소에 대한 유효성 검사 생각해 봐야함
           데이터베이스를 모킹해야하는가?에 대해 생각해 봐야함
   */

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

  @Test
  @DisplayName("SignUpDto의 email이 유효하지 않은 경우")
  public void signUpFailTest1() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (email)
    SignUpDto invalidUserSignUpDto = this.buildValidSignUpDto();
    invalidUserSignUpDto.setEmail("jinhokhkr");
    String requestBody = objectMapper.writeValueAsString(invalidUserSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."이다.
        // And: details는 {"email": "유효하지 않은 이메일 형식입니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 email이 이미 가입한 email인 경우")
  @Sql("classpath:oneUserInsert.sql")
  public void signUpFailTest2() throws Exception {
    // Given: email이 jinho@kh.kr인 User를 데이터베이스에 추가한다.
    assertThat(userRepository.findByEmail("jinho@kh.kr").orElse(null)).isNotNull();
    // Given: email이 jinho@kh.kr인 SignUpDto가 주어진다.
    SignUpDto validUserSignUpDto = this.buildValidSignUpDto();
    validUserSignUpDto.setEmail("jinho@kh.kr");
    String requestBody = objectMapper.writeValueAsString(validUserSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."이다.
        // And: details는 {"email": "이미 가입된 이메일입니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.DUPLICATE_EMAIL)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).hasSize(1);
  }

  @Test
  @DisplayName("SignUpDto의 password가 유효하지 않은 경우")
  public void signUpFailTest3() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (password)
    SignUpDto invalidUserSignUpDto = this.buildValidSignUpDto();
    invalidUserSignUpDto.setPassword("1234");
    String requestBody = objectMapper.writeValueAsString(invalidUserSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."이다.
        // And: details는 {"password": "영문, 숫자, 특수문자를 포함하여 8자 이상 20자 이하로 입력해주세요."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.password", is(Message.INVALID_PASSWORD)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 password와 passwordConfirm이 일치하지 않는 경우")
  public void signUpFailTest4() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (password)
    SignUpDto invalidUserSignUpDto = this.buildValidSignUpDto();
    invalidUserSignUpDto.setPasswordConfirm("q1w2e3r42@");
    String requestBody = objectMapper.writeValueAsString(invalidUserSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."이다.
        // And: details는 {"password": "비밀번호와 비밀번호 확인이 일치하지 않습니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.password", is(Message.INVALID_PASSWORD_CONFIRM)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 nickname이 유효하지 않은 경우")
  public void signUpFailTest5() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (nickname)
    SignUpDto invalidUserSignUpDto = this.buildValidSignUpDto();
    invalidUserSignUpDto.setNickname("ㅂㅈㄷㄱ");
    String requestBody = objectMapper.writeValueAsString(invalidUserSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."이다.
        // And: details는 {"nickname": "영문, 유효한 한글, 숫자를 이용하여 3자 이상 10자 이하로 입력해주세요."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", is(Message.INVALID_NICKNAME)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 nickname이 이미 가입한 nickname인 경우")
  @Sql("classpath:oneUserInsert.sql")
  public void signUpFailTest6() throws Exception {
    // Given: nickname이 nickname인 User를 데이터베이스에 추가한다.
    assertThat(userRepository.findByNickname("nickname").orElse(null)).isNotNull();
    // And: nickname이 nickname인 SignUpDto가 주어진다.
    SignUpDto validUserSignUpDto = this.buildValidSignUpDto();
    validUserSignUpDto.setNickname("nickname");
    validUserSignUpDto.setEmail("jinho2@kh.kr");
    String requestBody = objectMapper.writeValueAsString(validUserSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."이다.
        // And: details는 {"nickname": "이미 가입된 닉네임입니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", is(Message.DUPLICATE_NICKNAME)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).hasSize(1);
  }

  @Test
  @DisplayName("SignUpDto의 phone이 유효하지 않은 경우")
  public void signUpFailTest7() throws Exception {
    // Given: 유효하지 않은 SignUpDto가 주어진다. (phone)
    SignUpDto invalidUserSignUpDto = this.buildValidSignUpDto();
    invalidUserSignUpDto.setPhone("12345678912345");
    String requestBody = objectMapper.writeValueAsString(invalidUserSignUpDto);

    // When: SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해 주세요."이다.
        // And: details는 {"phone": "유효하지 않은 휴대폰 번호 형식입니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.phone", is(Message.INVALID_PHONE)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
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
