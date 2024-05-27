package com.kh.bookfinder.user.api;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.dto.DuplicateCheckDto;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SignUpDuplicateCheckApiTest {

  /* TODO: 중복된 이메일인 경우, Bad Request가 나을까? 아니면 Conflict(409)가 나을까?
   */

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;

  @Test
  @DisplayName("유효한 email이 주어지는 경우")
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
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.VALID_EMAIL)));
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
  @DisplayName("이미 가입한 email인 경우")
  public void emailDuplicateCheckFailTest2() throws Exception {
    // Given: field=email, value=jinho@kh.kr인 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("email")
        .value("jinho@kh.kr")
        .build();
    // And: UserRepository를 Mocking한다.
    when(userRepository.findByEmail(validDuplicateCheckDto.getValue()))
        .thenReturn(Optional.of(MockUser.getMockUser()));

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

  @Test
  @DisplayName("유효한 nickname이 주어지는 경우")
  public void nicknameDuplicateCheckSuccessTest() throws Exception {
    // Given: field=nickname, value=testNickname인 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("nickname")
        .value("testNickname")
        .build();

    // When: Duplicate Check API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/signup/duplicate")
            .param("field", validDuplicateCheckDto.getField())
            .param("value", validDuplicateCheckDto.getValue())
        )
        // Then: Status는 200 Ok이다.
        // And: message는 "가입이 가능한 닉네임입니다."이다.
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.VALID_NICKNAME)));
  }

  @Test
  @DisplayName("nickname 형식이 유효하지 않은 경우")
  public void nicknameDuplicateCheckFailTest1() throws Exception {
    // Given: field=nickname, value=ㅂㅈㄷㅁㅋ인 DuplicateCheckDto가 주어진다
    DuplicateCheckDto invalidDuplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("nickname")
        .value("ㅂㅈㄷㅁㅋ")
        .build();

    // When: Duplicate Check API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/signup/duplicate")
            .param("field", invalidDuplicateCheckDto.getField())
            .param("value", invalidDuplicateCheckDto.getValue())
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
        // And: details는 {"nickname": "영문, 유효한 한글, 숫자를 이용하여 3자 이상 10자 이하로 입력해주세요."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", is(Message.INVALID_NICKNAME)));
  }

  @Test
  @DisplayName("이미 가입한 닉네임인 경우")
  public void nicknameDuplicateCheckFailTest2() throws Exception {
    // Given: field=nickname, value=nickname인 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("nickname")
        .value("nickname")
        .build();
    // And: UserRepository를 Mocking한다.
    when(this.userRepository.findByNickname(validDuplicateCheckDto.getValue()))
        .thenReturn(Optional.of(MockUser.getMockUser()));

    // When: Duplicate Check API를 호출한다.
    this.mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/signup/duplicate")
            .param("field", validDuplicateCheckDto.getField())
            .param("value", validDuplicateCheckDto.getValue())
        )
        // Then: Status는 400 Bad Request 이다.
        // And: message는 "요청이 유효하지 않습니다. 다시 한번 확인해주세요."
        // And: details는 {"nickname": "이미 가입된 닉네임입니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.nickname", is(Message.DUPLICATE_NICKNAME)));
  }

  @Test
  @DisplayName("field가 email이나 nickname이 아닌 경우")
  public void duplicateCheckFailForDtoFieldTest() throws Exception {
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
        // And: details는 {"field": "field는 email이나 nickname만 가능합니다."}이다.
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.BAD_REQUEST)))
        .andExpect(MockMvcResultMatchers.jsonPath("$.details.field", is(Message.INVALID_FIELD)));
  }

  @Test
  @DisplayName("Header에 유효한 Authorization이 포함된 경우")
  public void duplicateCheckFailOnAuthorizationInHeaderTest() throws Exception {
    // Given: 유효한 DuplicateCheckDto가 주어진다.
    DuplicateCheckDto duplicateCheckDto = DuplicateCheckDto
        .builder()
        .field("email")
        .value("jinho@kh.kr")
        .build();
    // And: UserRepository를 Mocking 한다.
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(MockUser.getMockUser()));
    // And: JwtService를 Mocking한다.
    when(jwtService.validateToken(any())).thenReturn(true);

    // When: Header에 유효한 Authorization을 담아 Duplicate Check API를 호출한다.
    ResultActions resultActions = this.mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/signup/duplicate")
            .param("field", duplicateCheckDto.getField())
            .param("value", duplicateCheckDto.getValue())
            .header("Authorization", MockToken.mockAccessToken)
        );

    // Then: Status는 403 Forbidden 이다.
    resultActions.andExpect(MockMvcResultMatchers.status().isForbidden());
    // And: Response Body로 message와 detail이 반환된다.
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.message", is(Message.FORBIDDEN)));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.detail", is(Message.ALREADY_LOGIN)));
  }
}
