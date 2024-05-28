package com.kh.bookfinder.user.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.CONFLICT;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.user.dto.DuplicateCheckDto;
import com.kh.bookfinder.user.entity.User;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class SignUpDuplicateCheckApiTest {

  // 이메일,닉네임 중복검사 API

  @Autowired
  private MockMvc mockMvc;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;

  private DuplicateCheckDto getBaseEmailDuplicateCheckDto() {
    return DuplicateCheckDto
        .builder()
        .field("email")
        .value("jinho@kh.kr")
        .build();
  }

  private DuplicateCheckDto getBaseNicknameDuplicateCheckDto() {
    return DuplicateCheckDto
        .builder()
        .field("nickname")
        .value("고소하게")
        .build();
  }

  private ResultActions callApiWith(DuplicateCheckDto validDuplicateCheckDto) throws Exception {
    return mockMvc
        .perform(get("/api/v1/signup/duplicate")
            .param("field", validDuplicateCheckDto.getField())
            .param("value", validDuplicateCheckDto.getValue())
        );
  }

  @Test
  @DisplayName("유효한 email이 주어지는 경우")
  public void success_OnValidDuplicateCheckDto_WithEmail() throws Exception {
    // Given: 유효한 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = getBaseEmailDuplicateCheckDto();

    // When: Duplicate Check API를 호출한다.
    ResultActions resultActions = callApiWith(validDuplicateCheckDto);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.VALID_EMAIL)));
  }

  @Test
  @DisplayName("email 형식이 유효하지 않은 경우")
  public void fail_OnInvalidDuplicateCheckDto_WithEmail() throws Exception {
    // Given: 유효하지 않은 DuplicateCheckDto가 주어진다. (field=email)
    DuplicateCheckDto invalidDuplicateCheckDto = getBaseEmailDuplicateCheckDto();
    invalidDuplicateCheckDto.setValue("jinhokhkr");

    // When: Duplicate Check API를 호출한다.
    ResultActions resultActions = callApiWith(invalidDuplicateCheckDto);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.email", is(Message.INVALID_EMAIL)));
  }

  @Test
  @DisplayName("이미 가입한 email인 경우")
  public void fail_OnExistAlreadyInDB_WithEmailInDuplicateCheckDto() throws Exception {
    // Given: 유효한 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = getBaseEmailDuplicateCheckDto();
    // And: UserRepository가 email이 jinho@kh.kr인 mockUser를 반환하도록 Mocking한다.
    User mockUser = MockUser.getMockUser();
    mockUser.setEmail(validDuplicateCheckDto.getValue());
    when(userRepository.findByEmail(validDuplicateCheckDto.getValue())).thenReturn(Optional.of(mockUser));

    // When: Duplicate Check API를 호출한다.
    ResultActions resultActions = callApiWith(validDuplicateCheckDto);

    // Then: Status는 409 Conflict 이다.
    resultActions.andExpect(status().isConflict());
    // And: ResponseBody로 message와 detail를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(CONFLICT.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.DUPLICATE_EMAIL)));
  }

  @Test
  @DisplayName("유효한 nickname이 주어지는 경우")
  public void success_OnValidDuplicateCheckDto_WithNickname() throws Exception {
    // Given: 유효한 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = getBaseNicknameDuplicateCheckDto();

    // When: Duplicate Check API를 호출한다.
    ResultActions resultActions = callApiWith(validDuplicateCheckDto);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: ResponseBody로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.VALID_NICKNAME)));
  }

  @Test
  @DisplayName("nickname 형식이 유효하지 않은 경우")
  public void fail_OnInvalidDuplicateCheckDto_WithNickname() throws Exception {
    // Given: 유효하지 않은 DuplicateCheckDto가 주어진다. (field=nickname)
    DuplicateCheckDto invalidDuplicateCheckDto = getBaseNicknameDuplicateCheckDto();
    invalidDuplicateCheckDto.setValue("ㅂㅈㄷㅁㅋ");

    // When: Duplicate Check API를 호출한다.
    ResultActions resultActions = callApiWith(invalidDuplicateCheckDto);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.nickname", is(Message.INVALID_NICKNAME)));
  }

  @Test
  @DisplayName("이미 가입한 닉네임인 경우")
  public void fail_OnExistAlreadyInDB_WithNicknameInDuplicateCheckDto() throws Exception {
    // Given: 유효한 DuplicateCheckDto가 주어진다
    DuplicateCheckDto validDuplicateCheckDto = getBaseNicknameDuplicateCheckDto();
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    User mockUser = MockUser.getMockUser();
    mockUser.setNickname(validDuplicateCheckDto.getValue());
    when(userRepository.findByNickname(validDuplicateCheckDto.getValue())).thenReturn(Optional.of(mockUser));

    // When: Duplicate Check API를 호출한다.
    ResultActions resultActions = callApiWith(validDuplicateCheckDto);

    // Then: Status는 409 Conflict 이다.
    resultActions.andExpect(status().isConflict());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(CONFLICT.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.DUPLICATE_NICKNAME)));
  }

  @Test
  @DisplayName("field가 email이나 nickname이 아닌 경우")
  public void fail_OnInvalidDuplicateCheckDto_WithFieldNotEmailOrNickname() throws Exception {
    // Given: 유효하지 않은 DuplicateCheckDto가 주어진다. (field=invalid)
    DuplicateCheckDto invalidDuplicateCheckDto = getBaseEmailDuplicateCheckDto();
    invalidDuplicateCheckDto.setField("invalid");

    // When: Duplicate Check API를 호출한다.
    ResultActions resultActions = callApiWith(invalidDuplicateCheckDto);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: ResponseBody로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.field", is(Message.INVALID_FIELD)));
  }

  @Test
  @DisplayName("Header에 유효한 Authorization이 포함된 경우")
  public void fail_OnValidAuthorization_InHeader() throws Exception {
    // Given: 유효한 DuplicateCheckDto가 주어진다.
    DuplicateCheckDto duplicateCheckDto = getBaseEmailDuplicateCheckDto();
    // And: UserRepository가 mockUser를 반환하도록 Mocking한다.
    when(userRepository.findByEmail(any())).thenReturn(Optional.of(MockUser.getMockUser()));
    // And: JwtService가 true를 반환하도록 Mocking한다.
    when(jwtService.validateToken(any())).thenReturn(true);

    // When: Header에 유효한 Authorization을 담아 Duplicate Check API를 호출한다.
    ResultActions resultActions = mockMvc
        .perform(get("/api/v1/signup/duplicate")
            .param("field", duplicateCheckDto.getField())
            .param("value", duplicateCheckDto.getValue())
            .header("Authorization", "validToken")
        );

    // Then: Status는 403 Forbidden 이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail이 반환된다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.ALREADY_LOGIN)));
  }
}
