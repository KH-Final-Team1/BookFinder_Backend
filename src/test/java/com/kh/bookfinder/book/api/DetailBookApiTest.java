package com.kh.bookfinder.book.api;

import static com.kh.bookfinder.global.constants.HttpErrorMessage.BAD_REQUEST;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.FORBIDDEN;
import static com.kh.bookfinder.global.constants.HttpErrorMessage.NOT_FOUND;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.book.entity.Book;
import com.kh.bookfinder.book.enums.ApprovalStatus;
import com.kh.bookfinder.book.repository.BookRepository;
import com.kh.bookfinder.global.constants.Message;
import com.kh.bookfinder.helper.MockBook;
import com.kh.bookfinder.helper.MockUser;
import com.kh.bookfinder.user.entity.User;
import com.kh.bookfinder.user.entity.UserRole;
import com.kh.bookfinder.user.repository.UserRepository;
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
public class DetailBookApiTest {

  private static final String VALID_ACCESS_TOKEN = "validAccessToken";
  @Autowired
  private MockMvc mockMvc;
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
    when(jwtService.extractAccessToken(any())).thenReturn(VALID_ACCESS_TOKEN);
    when(jwtService.validateToken(VALID_ACCESS_TOKEN)).thenReturn(true);
    when(jwtService.extractEmail(VALID_ACCESS_TOKEN)).thenReturn(mockUser.getEmail());
    // And: UserRepository가 mockUser를 반환
    when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
  }

  private ResultActions callApi(Long isbn) throws Exception {
    return mockMvc.perform(get("/api/v1/books/{isbn}", isbn));
  }

  private ResultActions callApiWithAuth(Long isbn) throws Exception {
    return mockMvc.perform(get("/api/v1/books/{isbn}", isbn)
        .header("Authorization", "Bearer " + VALID_ACCESS_TOKEN));
  }

  @Test
  @DisplayName("유효한 isbn이 주어졌을 때")
  public void success_onValidIsbn() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;
    // And: mockBook이 주어진다.
    Book mockBook = MockBook.getMockBook();
    mockBook.setIsbn(validIsbn);

    // Mocking: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(validIsbn)).thenReturn(Optional.of(mockBook));

    // When: Detail Book API를 호출한다.
    ResultActions resultActions = callApi(validIsbn);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 Book 정보를 반환한다.
    assertBook(resultActions, mockBook);
  }

  @Test
  @DisplayName("유효하지 않은 isbn이 주어졌을 때")
  public void fail_onInvalidIsbn_withNotDigits13() throws Exception {
    // Given: 유효하지 않은 isbn이 주어진다.
    Long invalidIsbn = 12345678910L;
    // And: mockBook이 주어진다.
    Book mockBook = MockBook.getMockBook();
    mockBook.setIsbn(invalidIsbn);

    // When: Detail Book API를 호출한다.
    ResultActions resultActions = callApi(invalidIsbn);

    // Then: Status는 400 Bad Request이다.
    resultActions.andExpect(status().isBadRequest());
    // And: Response Body로 message와 details를 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(BAD_REQUEST.getMessage())));
    resultActions.andExpect(jsonPath("$.details.isbn", is(Message.INVALID_ISBN_DIGITS)));
  }

  @Test
  @DisplayName("isbn에 대한 튜플이 없을 때")
  public void fail_onNotExistInDB_withIsbn() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;

    // When: Detail Book API를 호출한다.
    ResultActions resultActions = callApi(validIsbn);

    // Then: Status는 404 Not Found이다.
    resultActions.andExpect(status().isNotFound());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(NOT_FOUND.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_FOUND_BOOK)));
  }

  @Test
  @DisplayName("isbn에 대한 book의 status가 approve가 아닐 때")
  public void fail_onStatusIsNotApprove_withIsbn() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;
    // And: mockBook이 주어진다.
    Book mockBook = MockBook.getMockBook(ApprovalStatus.WAIT);
    mockBook.setIsbn(validIsbn);

    // Mocking: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(validIsbn)).thenReturn(Optional.of(mockBook));

    // When: Detail Book API를 호출한다.
    ResultActions resultActions = callApi(validIsbn);

    // Then: Status는 403 Forbidden이다.
    resultActions.andExpect(status().isForbidden());
    // And: Response Body로 message와 detail을 반환한다.
    resultActions.andExpect(jsonPath("$.message", is(FORBIDDEN.getMessage())));
    resultActions.andExpect(jsonPath("$.detail", is(Message.NOT_APPROVED_BOOK)));
  }

  @Test
  @DisplayName("관리자 권한이고, isbn에 대한 book의 status가 approve가 아닐 때")
  public void success_onStatusIsNotApprove_withIsbn_andWithAdminAuth() throws Exception {
    // Given: 유효한 isbn이 주어진다.
    Long validIsbn = 1234567891011L;
    // And: mockBook이 주어진다.
    Book mockBook = MockBook.getMockBook(ApprovalStatus.WAIT);
    mockBook.setIsbn(validIsbn);

    // Mocking: JwtAuthenticationFilter
    mockJwtAuthenticationFilter(MockUser.getMockUser(UserRole.ROLE_ADMIN));
    // And: BookRepository가 mockBook을 반환
    when(bookRepository.findByIsbn(validIsbn)).thenReturn(Optional.of(mockBook));

    // When: Detail Book API를 호출한다.
    ResultActions resultActions = callApiWithAuth(validIsbn);

    // Then: Status는 200 Ok이다.
    resultActions.andExpect(status().isOk());
    // And: Response Body로 Book 정보를 반환한다.
    assertBook(resultActions, mockBook);
  }

  private void assertBook(ResultActions resultActions, Book expected) throws Exception {
    resultActions.andExpect(jsonPath("$.isbn", is(expected.getIsbn())));
    resultActions.andExpect(jsonPath("$.additionSymbol", is(expected.getAdditionSymbol())));
    resultActions.andExpect(jsonPath("$.name", is(expected.getName())));
    resultActions.andExpect(jsonPath("$.authors", is(expected.getAuthors())));
    resultActions.andExpect(jsonPath("$.publisher", is(expected.getPublisher())));
    resultActions.andExpect(jsonPath("$.publicationYear", is(expected.getPublicationYear())));
    resultActions.andExpect(jsonPath("$.classNo", is(expected.getClassNo())));
    resultActions.andExpect(jsonPath("$.className", is(expected.getClassName())));
    resultActions.andExpect(jsonPath("$.description", is(expected.getDescription())));
    resultActions.andExpect(jsonPath("$.imageUrl", is(expected.getImageUrl())));
    resultActions.andExpect(jsonPath("$.approvalStatus", is(expected.getApprovalStatus().name())));
  }
}
