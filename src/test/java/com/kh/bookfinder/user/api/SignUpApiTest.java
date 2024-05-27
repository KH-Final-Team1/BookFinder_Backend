package com.kh.bookfinder.user.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.borough.entity.Borough;
import com.kh.bookfinder.borough.repository.BoroughRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.dto.SignUpDto;
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
public class SignUpApiTest {

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

  @Test
  @DisplayName("유효한 SignUpDto가 주어지는 경우")
  public void signUpSuccessTestOnValidSignupDto() throws Exception {
    // Given: 유효한 SignUpDto가 주어진다.
    SignUpDto validSignUpDto = this.buildValidSignUpDto();
    String requestBody = objectMapper.writeValueAsString(validSignUpDto);
    // And: Borough가 mockBorough를 반환하도록 Mocking한다.
    when(boroughRepository.findByName(any()))
        .thenReturn(Optional.of(Borough.builder().id(5L).name("관악구").build()));

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
  }

  @Test
  @DisplayName("SignUpDto의 email이 유효하지 않은 경우")
  public void signUpFailTestOnInvalidEmailInSignupDto() throws Exception {
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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 email이 이미 가입한 email인 경우")
  public void signUpFailTestOnDuplicateEmailInSignupDto() throws Exception {
    // Given: email이 jinho@kh.kr인 SignUpDto가 주어진다.
    SignUpDto validUserSignUpDto = this.buildValidSignUpDto();
    validUserSignUpDto.setEmail("jinho@kh.kr");
    String requestBody = objectMapper.writeValueAsString(validUserSignUpDto);
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByEmail(validUserSignUpDto.getEmail()))
        .thenReturn(Optional.of(MockUser.getMockUser()));

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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.email", is(Message.DUPLICATE_EMAIL)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 password가 유효하지 않은 경우")
  public void signUpFailTestOnInvalidPasswordInSignupDto() throws Exception {
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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.password", is(Message.INVALID_PASSWORD)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 password와 passwordConfirm이 일치하지 않는 경우")
  public void signUpFailTestOnNotEqualPasswordWithPasswordConfirmInSignupDto() throws Exception {
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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.password", is(Message.INVALID_PASSWORD_CONFIRM)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 nickname이 유효하지 않은 경우")
  public void signUpFailTestOnInvalidNicknameInSignupDto() throws Exception {
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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", is(Message.INVALID_NICKNAME)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 nickname이 이미 가입한 nickname인 경우")
  public void signUpFailTestOnDuplicateNicknameInSignupDto() throws Exception {
    // Given: nickname이 nickname인 SignUpDto가 주어진다.
    SignUpDto validUserSignUpDto = this.buildValidSignUpDto();
    validUserSignUpDto.setNickname("nickname");
    String requestBody = objectMapper.writeValueAsString(validUserSignUpDto);
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByNickname(validUserSignUpDto.getNickname()))
        .thenReturn(Optional.of(MockUser.getMockUser()));

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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", is(Message.DUPLICATE_NICKNAME)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("SignUpDto의 phone이 유효하지 않은 경우")
  public void signUpFailTestOnInvalidPhoneInSignupDto() throws Exception {
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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(BAD_REQUEST.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.phone", is(Message.INVALID_PHONE)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }

  @Test
  @DisplayName("Header에 유효한 Authorization이 포함된 경우")
  public void signUpFailTestOnAuthorizationInHeader() throws Exception {
    // Given: 유효한 SignUpDto가 주어진다.
    SignUpDto validSignUpDto = this.buildValidSignUpDto();
    String requestBody = objectMapper.writeValueAsString(validSignUpDto);
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByEmail(any()))
        .thenReturn(Optional.of(MockUser.getMockUser()));
    // And: JwtService를 Mocking한다.
    when(jwtService.validateToken(any())).thenReturn(true);

    // When: Header에 유효한 Authorization을 담아 SignUp API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders
            .post("/api/v1/signup")
            .content(requestBody)
            .contentType("application/json")
            .header("Authorization", "validToken")
        )
        // Then: Status는 403 Forbidden 이다.
        .andExpect(MockMvcResultMatchers.status().isForbidden())
        // And: Response Body로 message와 detail가 반환된다.
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(FORBIDDEN.getMessage())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.ALREADY_LOGIN)));
    // And: 데이터베이스에 해당 User 정보가 저장되지 않는다.
    assertThat(userRepository.findAll()).isEmpty();
  }


  private SignUpDto buildValidSignUpDto() {
    return SignUpDto.builder()
        .email("jinho@kh.kr")
        .password("q1w2e3r41!")
        .passwordConfirm("q1w2e3r41!")
        .nickname("nickname")
        .address("서울시 관악구 어딘가")
        .phone("01012345678")
        .build();
  }
}
