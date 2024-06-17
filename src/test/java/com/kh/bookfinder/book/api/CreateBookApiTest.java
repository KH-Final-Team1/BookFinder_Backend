package com.kh.bookfinder.book.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.CONFLICT;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.dto.BookCreateRequestDto;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
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
public class CreateBookApiTest {

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

  private ResultActions callApiWithAuth(BookCreateRequestDto requestDto) throws Exception {
    String requestBody = objectMapper.writeValueAsString(requestDto);
    return mockMvc.perform(post("/api/v1/books")
        .contentType("application/json")
        .header("Authorization", "Bearer " + ACCESS_TOKEN)
        .content(requestBody));
  }

  @Test
  @DisplayName("유효한 BookCreateRequestDto가 주어졌을 때")
  public void success_onValidBookCreateRequestDto() throws Exception {
    // Given: 유효한 BookCreateRequestDto가 주어진다.
    BookCreateRequestDto validRequestDto = RequestDto.baseBookCreateRequestDto();

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_USER));

    // When: Create Book API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto);

    // Then: Status는 201 Created이다.
    resultActions.andExpect(status().isCreated());
    // And: Response Body로 message를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(Message.SUCCESS_BOOK_REQUEST)));
  }

  @Test
  @DisplayName("유효하지 않은 BookCreateRequestDto가 주어졌을 때")
  public void fail_onInvalidBookCreateRequestDto() throws Exception {
    // Given: 유효하지 않은 BookCreateRequestDto가 주어진다.
    BookCreateRequestDto invalidRequestDto = RequestDto.baseBookCreateRequestDto();
    invalidRequestDto.setIsbn(123L);
    invalidRequestDto.setName(null);
    invalidRequestDto.setImageUrl(null);
    invalidRequestDto.setAuthors(null);
    invalidRequestDto.setPublisher(null);
    invalidRequestDto.setPublicationYear(null);
    invalidRequestDto.setClassNo(null);
    invalidRequestDto.setClassName(null);
    invalidRequestDto.setDescription(null);

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_USER));

    // When: Create Book API를 호출한다.
    ResultActions resultActions = callApiWithAuth(invalidRequestDto);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.isbn", is(Message.INVALID_ISBN_DIGITS)));
    resultActions.andExpect(jsonPath("$.details.name", containsString("null")));
    resultActions.andExpect(jsonPath("$.details.imageUrl", containsString("null")));
    resultActions.andExpect(jsonPath("$.details.authors", containsString("null")));
    resultActions.andExpect(jsonPath("$.details.publisher", containsString("null")));
    resultActions.andExpect(jsonPath("$.details.publicationYear", containsString("null")));
    resultActions.andExpect(jsonPath("$.details.classNo", containsString("null")));
    resultActions.andExpect(jsonPath("$.details.className", containsString("null")));
    resultActions.andExpect(jsonPath("$.details.description", containsString("null")));
  }

  @Test
  @DisplayName("isbn에 대한 데이터가 이미 존재하고 approvalStatus가 WAIT일 때")
  public void fail_onAlreadyExistInDB_andApprovalStatusIsWait() throws Exception {
    // Given: 유효한 BookCreateRequestDto가 주어진다.
    BookCreateRequestDto validRequestDto = RequestDto.baseBookCreateRequestDto();
    Book mockBook = validRequestDto.toEntity();

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_USER));
    // And: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(mockBook.getIsbn())).thenReturn(Optional.of(mockBook));

    // When: Create Book API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto);

    // Then: Status는 409 Conflict이다.
    resultActions.andExpect(status().isConflict());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(CONFLICT.getMessage())));
    String detail = Message.DUPLICATE_BOOK + " (" + ApprovalStatus.WAIT + ")";
    resultActions.andExpect(jsonPath("$.detail", is(detail)));
  }

  @Test
  @DisplayName("isbn에 대한 데이터가 이미 존재하고 approvalStatus가 APPROVE일 때")
  public void fail_onAlreadyExistInDB_andApprovalStatusIsApprove() throws Exception {
    // Given: 유효한 BookCreateRequestDto가 주어진다.
    BookCreateRequestDto validRequestDto = RequestDto.baseBookCreateRequestDto();
    Book mockBook = validRequestDto.toEntity();
    mockBook.setApprovalStatus(ApprovalStatus.APPROVE);

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_USER));
    // And: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(mockBook.getIsbn())).thenReturn(Optional.of(mockBook));

    // When: Create Book API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto);

    // Then: Status는 409 Conflict이다.
    resultActions.andExpect(status().isConflict());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(CONFLICT.getMessage())));
    String detail = Message.DUPLICATE_BOOK + " (" + ApprovalStatus.APPROVE + ")";
    resultActions.andExpect(jsonPath("$.detail", is(detail)));
  }

  @Test
  @DisplayName("isbn에 대한 데이터가 이미 존재하고 approvalStatus가 REJECT일 때")
  public void fail_onAlreadyExistInDB_andApprovalStatusIsReject() throws Exception {
    // Given: 유효한 BookCreateRequestDto가 주어진다.
    BookCreateRequestDto validRequestDto = RequestDto.baseBookCreateRequestDto();
    Book mockBook = validRequestDto.toEntity();
    mockBook.setApprovalStatus(ApprovalStatus.REJECT);

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_USER));
    // And: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(mockBook.getIsbn())).thenReturn(Optional.of(mockBook));

    // When: Create Book API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto);

    // Then: Status는 409 Conflict이다.
    resultActions.andExpect(status().isConflict());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(CONFLICT.getMessage())));
    String detail = Message.DUPLICATE_BOOK + " (" + ApprovalStatus.REJECT + ")";
    resultActions.andExpect(jsonPath("$.detail", is(detail)));
  }

  @Test
  @DisplayName("권한 없이 요청하는 경우")
  public void fail_withNoAuth() throws Exception {
    // Given: 유효한 BookCreateRequestDto가 주어진다.
    BookCreateRequestDto validRequestDto = RequestDto.baseBookCreateRequestDto();
    Book mockBook = validRequestDto.toEntity();

    // Mocking: JwtService가 JwtException을 발생
    when(jwtService.validateToken(any())).thenThrow(new JwtException(Message.NOT_LOGIN));
    // And: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(mockBook.getIsbn())).thenReturn(Optional.of(mockBook));

    // When: Create Book API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validRequestDto);

    // Then: Status는 401 Unauthorized이다.
    resultActions.andExpect(status().isUnauthorized());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(UNAUTHORIZED.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_LOGIN)));
  }
}
