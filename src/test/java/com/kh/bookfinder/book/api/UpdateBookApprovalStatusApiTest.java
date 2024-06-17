package com.kh.bookfinder.book.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.dto.BookUpdateStatusRequestDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBook;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.helper.RequestDto;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
import io.jsonwebtoken.JwtException;
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
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class UpdateBookApprovalStatusApiTest {

  private static final String ACCESS_TOKEN = "testAccessToken";
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;
  @MockBean
  private BookRepository bookRepository;
  @MockBean
  private UserRepository userRepository;
  @MockBean
  private JwtService jwtService;


  private void mockJwtAuthenticationFilter(User mockUser) {
    // JwtAuthenticationFilter Mocking
    // extractAccessToken() -> "validAccessToken"
    // validateToken() -> true
    // extractEmail() -> mockUser's email;
    when(jwtService.extractAccessToken(any())).thenReturn(ACCESS_TOKEN);
    when(jwtService.validateToken(ACCESS_TOKEN)).thenReturn(true);
    when(jwtService.extractEmail(ACCESS_TOKEN)).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
  }

  private ResultActions callApiWithAuth(Long isbn, BookUpdateStatusRequestDto requestDto) throws Exception {
    String requestBody = objectMapper.writeValueAsString(requestDto);
    return mockMvc.perform(patch("/api/v1/books/{isbn}", isbn)
        .contentType("application/json")
        .header("Authorization", "Bearer " + ACCESS_TOKEN)
        .content(requestBody));
  }

  @Test
  @DisplayName("관리자 권한이고, 유효한 isbn과 유효한 BookUpdateStatusDto가 주어질 때")
  public void success_onValidIsbn_andValidBookUpdateStatusRequestDto_andWithAdminAuth() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;
    // And: 유효한 BookUpdateStatusDto가 주어진다.
    BookUpdateStatusRequestDto validBookUpdateStatusRequestDto =
        RequestDto.baseBookUpdateStatusDto(ApprovalStatus.APPROVE.name());
    // And: mockBook이 주어진다.
    Book mockBook = MockBook.getMockBook(ApprovalStatus.WAIT);

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));
    // And: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(validIsbn)).thenReturn(Optional.of(mockBook));

    // When: Update Book Approval Status API 호출
    ResultActions resultActions = callApiWithAuth(validIsbn, validBookUpdateStatusRequestDto);

    // Then: Status는 200 Ok 이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.UPDATE_APPROVAL_STATUS)));
  }

  @Test
  @DisplayName("유효하지 않은 isbn이 주어질 때")
  public void fail_onInvalidIsbn() throws Exception {
    // Given: 유효하지 않은 isbn이 주어진다.
    Long invalidIsbn = 12345011L;
    // And: 유효한 BookUpdateStatusDto가 주어진다.
    BookUpdateStatusRequestDto validBookUpdateStatusRequestDto =
        RequestDto.baseBookUpdateStatusDto(ApprovalStatus.APPROVE.name());

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));

    // When: Update Book Approval Status API 호출
    ResultActions resultActions = callApiWithAuth(invalidIsbn, validBookUpdateStatusRequestDto);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.isbn", is(Message.INVALID_ISBN_DIGITS)));
  }

  @Test
  @DisplayName("유효하지 않은 BookUpdateRequestDto가 주어질 때")
  public void fail_onInvalidBookUpdateRequestDto_withApprovalStatus() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;
    // And: 유효하지 않은 BookUpdateStatusDto가 주어진다.
    BookUpdateStatusRequestDto invalidBookUpdateStatusRequestDto =
        RequestDto.baseBookUpdateStatusDto("invalid");

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));

    // When: Update Book Approval Status API 호출
    ResultActions resultActions = callApiWithAuth(validIsbn, invalidBookUpdateStatusRequestDto);

    // Then: Status는 400 Bad Request 이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.approvalStatus", is(Message.INVALID_APPROVAL_STATUS)));
  }

  @Test
  @DisplayName("관리자 권한이 아닌 경우")
  public void fail_withNotAdminAuth() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;
    // And: 유효한 BookUpdateStatusDto가 주어진다.
    BookUpdateStatusRequestDto validBookUpdateStatusRequestDto =
        RequestDto.baseBookUpdateStatusDto(ApprovalStatus.APPROVE.name());

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_USER));

    // When: Update Book Approval Status API 호출
    ResultActions resultActions = callApiWithAuth(validIsbn, validBookUpdateStatusRequestDto);

    // Then: Status는 403 Forbidden 이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.FORBIDDEN_BOOK_STATUS_UPDATE)));
  }

  @Test
  @DisplayName("권한이 없는 경우")
  public void fail_withNoAuth() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;
    // And: 유효한 BookUpdateStatusDto가 주어진다.
    BookUpdateStatusRequestDto validBookUpdateStatusRequestDto =
        RequestDto.baseBookUpdateStatusDto(ApprovalStatus.APPROVE.name());

    // Mocking: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));

    // When: Update Book Approval Status API 호출
    ResultActions resultActions = callApiWithAuth(validIsbn, validBookUpdateStatusRequestDto);

    // Then: Status는 401 Unauthorized 이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }
}
